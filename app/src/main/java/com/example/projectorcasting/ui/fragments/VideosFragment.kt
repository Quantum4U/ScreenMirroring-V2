package com.example.projectorcasting.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectorcasting.AnalyticsConstant
import com.example.projectorcasting.adapter.VideoHorizontalAdapter
import com.example.projectorcasting.adapter.VideoSectionalAdapter
import com.example.projectorcasting.casting.model.CastModel
import com.example.projectorcasting.casting.queue.QueueDataProvider
import com.example.projectorcasting.casting.utils.CastHelper
import com.example.projectorcasting.casting.utils.Utils
import com.example.projectorcasting.models.MediaData
import com.example.projectorcasting.models.SectionModel
import com.example.projectorcasting.prefrences.AppPreference
import com.example.projectorcasting.utils.AppConstants
import com.example.projectorcasting.utils.AppUtils
import com.example.projectorcasting.utils.MediaListSingleton
import com.example.projectorcasting.utils.PromptHelper
import com.example.projectorcasting.viewmodels.VideoViewModel
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.cast.framework.CastState
import com.quantum.projector.screenmirroring.cast.casting.phoneprojector.videoprojector.casttv.castforchromecast.screencast.casttotv.R
import com.quantum.projector.screenmirroring.cast.casting.phoneprojector.videoprojector.casttv.castforchromecast.screencast.casttotv.databinding.FragmentVideosBinding
import engine.app.adshandler.AHandler
import engine.app.analytics.logGAEvents
import engine.app.listener.OnRewardedEarnedItem
import engine.app.server.v2.Slave
import io.github.dkbai.tinyhttpd.nanohttpd.core.util.PathSingleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class VideosFragment : BaseFragment(R.layout.fragment_videos) {

    private val videoViewModel: VideoViewModel by viewModels()
    private var binding: FragmentVideosBinding? = null

    private var videoAdapter: VideoHorizontalAdapter? = null
    private var videoSectionalAdapter: VideoSectionalAdapter? = null
    private var mediaMapList: ArrayList<SectionModel>? = null
    private var isCastConnected = false
    private var itemMediaData: MediaData? = null
    private var appPreference: AppPreference? = null
    private var isRewardedCompleted = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentVideosBinding.bind(view)
        appPreference = context?.let { AppPreference(it) }

        observeCastingLiveData()
        observeVideoList()
        doFetchingWork()
//        observeMediaThumbnailCreated(thumb)
        getConnectionStatus()

        binding?.llSorting?.setOnClickListener {
            logGAEvents(AnalyticsConstant.GA_Videos_Date_Sorting)
            sortMediaList()
        }

        binding?.llConnect?.setOnClickListener {
            logGAEvents(AnalyticsConstant.GA_Videos_Cast_Connect)
//            openScanDevicePage()
            PromptHelper.showCastingPrompt(
                context,
                ::castPromtAction,
                isCastConnected,
                itemMediaData
            )
        }

        binding?.llConnected?.setOnClickListener {
            logGAEvents(AnalyticsConstant.GA_Videos_Cast_DisConnect)
            videoViewModel.showConnectionPrompt(context, ::actionPerform, false, null)
        }

        binding?.ivBack?.setOnClickListener {
            if (binding?.searchView?.isIconified == true)
                exitPage()
            else {
                binding?.rlToolbarLayout?.visibility = View.VISIBLE
                binding?.searchView?.setQuery("", false)
                binding?.searchView?.isIconified = true
            }
        }

        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    exitPage()
                }
            })


        binding?.tvQueued?.setOnClickListener {
            logGAEvents(AnalyticsConstant.GA_Videos_Queue_Button)
            findNavController().navigate(R.id.nav_queue)
            showNavigationFullAds(activity)
        }

        val provider: QueueDataProvider? = QueueDataProvider.getInstance(context)
        if (appPreference?.isImageCasting() == true && provider?.count!! > 0)
            provider.removeAll()

        if (provider?.count!! > 0) {
            binding?.tvQueued?.visibility = View.VISIBLE
        } else
            binding?.tvQueued?.visibility = View.GONE


        Log.d(
            "VideosFragment",
            "onViewCreated A13 : <><>" + provider?.currentItemId + "//" + provider?.getItem(0)?.media?.metadata?.mediaType
        )
        Log.d(
            "VideosFragment",
            "onViewCreated A13 : <><>" + provider?.getPositionByItemId(1) + "//" + provider?.count
        )

        setBrowserValue()

        binding?.searchView?.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // searchView expanded
                binding?.rlToolbarLayout?.visibility = View.GONE
            } else {
                // searchView not expanded
                binding?.rlToolbarLayout?.visibility = View.VISIBLE
                binding?.searchView?.setQuery("", false)
                binding?.searchView?.isIconified = true
            }
        }

        binding?.searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText.toString())
                return false
            }
        })
    }

    private fun filter(text: String) {
        Log.d("VideosFragment", "filter A13 : >>" + mediaMapList?.size)
        if (text.isEmpty()) {
            binding?.rvHorizontal?.visibility = View.VISIBLE
            binding?.rlSorting?.visibility = View.VISIBLE
            binding?.rvVertical?.visibility = View.VISIBLE
            binding?.tvNoVideosFound?.visibility = View.GONE
            videoSectionalAdapter?.refreshList(mediaMapList)
            return
        } else {
            binding?.rvHorizontal?.visibility = View.GONE
            binding?.rlSorting?.visibility = View.GONE
        }

        var filteredDataList: ArrayList<MediaData>? = arrayListOf()

        Log.d(
            "VideosFragment",
            "filter A13 : >>" + "//" + filteredDataList?.size + mediaMapList?.size
        )
        for (section in mediaMapList!!) {
            var filteredList: ArrayList<SectionModel>? = arrayListOf()

            for (data in section.sectionList!!) {
                Log.d(
                    "VideosFragment",
                    "filter A13 : >>11<<" + data.file?.name + "//" + text + "//" + data.file?.name?.lowercase()
                        ?.trim()?.contains(text.lowercase().trim())
                )
                if (data.file?.name?.lowercase()?.trim()
                        ?.contains(text.lowercase().trim()) == true
                ) {
                    // if the item is matched we are
                    // adding it to our filtered list.
                    filteredDataList?.add(data)
                }
            }

            filteredList?.add(SectionModel(null, filteredDataList))
            Log.d(
                "VideosFragment",
                "filter A13 : >>22<<" + filteredList?.size + "//" + filteredDataList?.size + mediaMapList?.size
            )

            if (filteredList?.isEmpty() == true) {
                // if no item is added in filtered list we are
                // displaying a message as no data found.
                binding?.rvVertical?.visibility = View.GONE
                binding?.tvNoVideosFound?.visibility = View.VISIBLE
            } else {
                // at last we are passing that filtered
                // list to our adapter class.
                binding?.rvVertical?.visibility = View.VISIBLE
                binding?.tvNoVideosFound?.visibility = View.GONE
                videoSectionalAdapter?.filtereList(filteredList)
            }
        }
    }

    private fun openScanDevicePage() {
        findNavController().navigate(R.id.nav_scan_device)
        showFullAds(activity)
    }

    private fun doFetchingWork() {
        val pathList = MediaListSingleton.getGalleryVideoList()
        if (pathList != null && pathList.isNotEmpty()) {
            setAdapters(pathList)
        } else {
            if (getDashViewModel()?.isLoading == true)
                showLoader()
            else {
                showLoader()
                context?.let { getDashViewModel()?.fetchVideoList(it) }
            }
        }
    }

    private fun observeCastingLiveData() {
        castingLiveData().observe(viewLifecycleOwner, Observer { state ->
            if (state == CastState.CONNECTED) {
                isCastConnected = true
//                binding?.llConnected?.visibility = View.VISIBLE
//                binding?.llConnect?.visibility = View.GONE
//                binding?.tvConnected?.text = getString(R.string.connected, getConnectedDeviceName())
                binding?.ivCasting?.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_cast_enable,
                        null
                    )
                )
            } else if (state == CastState.NOT_CONNECTED) {
                isCastConnected = false
//                binding?.llConnected?.visibility = View.GONE
//                binding?.llConnect?.visibility = View.VISIBLE
                binding?.ivCasting?.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_cast_disable,
                        null
                    )
                )
            }

            setBrowserValue()

        })
    }

    private fun getConnectionStatus() {
        isCastConnected = isCastingConnected() == true
        if (isCastConnected)
            binding?.ivCasting?.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_cast_enable,
                    null
                )
            )
        else
            binding?.ivCasting?.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_cast_disable,
                    null
                )
            )
    }

    private fun setBrowserValue() {
        if (isServerRunning()) {
            binding?.ivBrowser?.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_browser_enable,
                    null
                )
            )
        } else {
            binding?.ivBrowser?.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_browser_disable,
                    null
                )
            )
        }
    }

    private fun observeVideoList() {
        getDashViewModel()?.videosList?.observe(viewLifecycleOwner, Observer { videoList ->
            hideLoader()
            setAdapters(videoList)
        })
    }

    private fun setAdapters(videoList: List<MediaData>) {
        if (videoList.isNotEmpty()) {

            binding?.rlView?.visibility = View.VISIBLE
            binding?.tvNoVideosFound?.visibility = View.GONE

            if (videoList.size <= AppConstants.MAX_HORIZONTAL_ITEM) {
                binding?.rlSorting?.visibility = View.GONE
                binding?.rvVertical?.visibility = View.GONE
            } else {
                binding?.rlSorting?.visibility = View.VISIBLE
                binding?.rvVertical?.visibility = View.VISIBLE

                setSectionAdapter()
            }

            var layoutManagerTopProducts = object : LinearLayoutManager(requireContext()) {

            }

            layoutManagerTopProducts.orientation = LinearLayoutManager.HORIZONTAL
            binding?.rvHorizontal?.layoutManager = layoutManagerTopProducts
            videoAdapter = VideoHorizontalAdapter(videoList, ::itemClick)
            binding?.rvHorizontal?.adapter = videoAdapter
        } else {
            binding?.rlView?.visibility = View.GONE
            binding?.tvNoVideosFound?.visibility = View.VISIBLE
        }

    }

    private fun playMedia(thumbFile: File?, mediaData: MediaData?) {
        hideLoader()
        appPreference?.setImageCasting(false)
        val thumbPath = thumbFile?.path?.split("0/")?.get(1)
        val path = mediaData?.file?.path?.split("0/")?.get(1)
        path?.let {
            CastHelper.playMedia(
                context, mediaData, it,
                thumbPath.toString(), Utils.VIDEO, ::checkForQueue, getConnectedDeviceName()
            )
        }

        //set path for html
        showVideoInHtml(mediaData)
    }

    private fun showVideoInHtml(mediaData: MediaData?) {
        val path = mediaData?.file?.path?.split("0/")?.get(1)
        var pathList: java.util.ArrayList<String> = arrayListOf()
        pathList.add(path.toString())
        PathSingleton.setVideoPath(pathList)
        PathSingleton.setAudioPath(null)
        PathSingleton.setImagePath(null)
    }

    private fun itemClick(mediaData: MediaData) {
        itemMediaData = mediaData
        if (isCastConnected) {
            showLoader()
            CoroutineScope(Dispatchers.IO).launch {
                val thumb = AppUtils.saveTempThumb(context, mediaData.bitmap)

                withContext(Dispatchers.Main) {
                    playMedia(thumb, mediaData)
                }
            }
        } else {
            PromptHelper.showCastingPrompt(context, ::castPromtAction, isCastConnected, mediaData)
        }
    }

    private fun setSectionAdapter() {
        mediaMapList = MediaListSingleton.getGalleryVideoSectionedList()
        videoSectionalAdapter =
            VideoSectionalAdapter(requireContext(), mediaMapList, ::itemClick)
        val layoutManager = GridLayoutManager(requireContext(), 1)
        videoSectionalAdapter?.setLayoutManager(layoutManager)
        videoSectionalAdapter?.shouldShowHeadersForEmptySections(true)
        binding?.rvVertical?.hasFixedSize()
        binding?.rvVertical?.layoutManager = layoutManager
        binding?.rvVertical?.adapter = videoSectionalAdapter
    }

    private fun sortMediaList() {
        if (binding?.tvSortingText?.text?.equals(getString(R.string.ascending)) == true) {
            binding?.ivSortingIcon?.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_descending_icon,
                    null
                )
            )
            binding?.tvSortingText?.text = getString(R.string.descending)
        } else {
            binding?.ivSortingIcon?.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_ascending_icon,
                    null
                )
            )
            binding?.tvSortingText?.text = getString(R.string.ascending)
        }

        val adapterList = videoSectionalAdapter?.getItemList()?.reversed()
        videoSectionalAdapter?.refreshList(adapterList as ArrayList<SectionModel>?)
    }

    private fun actionPerform(isConnect: Boolean, castModel: CastModel?) {
        if (isConnect)
            startCasting(castModel?.routeInfo, castModel?.castDevice)
        else
            stopCasting()
    }

    private fun castPromtAction(isCastDeviceClick: Boolean, mediaData: MediaData?) {
        if (Slave.hasPurchased(context)) {
            openPageForConnection(isCastDeviceClick,mediaData)
        }else{
            PromptHelper.showInappBindPrompt(context,::premiumPromptAction,isCastDeviceClick,mediaData)
        }
    }

    private fun premiumPromptAction(isGoPremium: Boolean,isCastDeviceClick: Boolean, mediaData: MediaData?) {
        if (isGoPremium){
            AHandler.getInstance().showRemoveAdsPrompt(context)
        }else{
//            appPreference?.setCastingCount(appPreference?.getCastingCount()?.minus(1) ?: 0)
            AHandler.getInstance().showRewardedVideoOrFullAds(activity, true, object : OnRewardedEarnedItem{
                override fun onRewardedLoaded() {

                }

                override fun onRewardedFailed(msg: String?) {
                    openPageForConnection(isCastDeviceClick,mediaData)
                }

                override fun onUserEarnedReward(reward: RewardItem?) {
                    isRewardedCompleted = true
                }

                override fun onRewardAdsDismiss() {
                    if (isRewardedCompleted) {
                        isRewardedCompleted = false
                        openPageForConnection(isCastDeviceClick, mediaData)
                    }
                }

            })
        }
    }

    private fun openPageForConnection(isCastDeviceClick: Boolean, mediaData: MediaData?) {
        if (isCastDeviceClick) {
//            if (!isCastConnected)
            findNavController().navigate(R.id.nav_scan_device)
            showFullAds(activity)
//            else
//                stopCasting()
        } else {
            showVideoInHtml(mediaData)
            openBrowserPage()
        }
    }

    private fun checkForQueue(count: Int, openBrowser: Boolean, openDeviceListPage: Boolean) {
        Log.d("VideosFragment", "onViewCreated A13 : ><<<" + count)
        if (count > 0) {
            binding?.tvQueued?.visibility = View.VISIBLE
        }

        if (openBrowser) {
            openBrowserPage()
        }

        if (openDeviceListPage) {
            openScanDevicePage()
        }

    }

    private fun openBrowserPage() {
        findNavController().navigate(R.id.nav_browse_cast)
        showFullAds(activity)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun exitPage() {
        findNavController().navigateUp()
    }

}