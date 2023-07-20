package com.example.projectorcasting.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectorcasting.AnalyticsConstant
import com.example.projectorcasting.adapter.AudioAdapter
import com.example.projectorcasting.casting.model.CastModel
import com.example.projectorcasting.casting.queue.QueueDataProvider
import com.example.projectorcasting.casting.utils.CastHelper
import com.example.projectorcasting.casting.utils.Utils
import com.example.projectorcasting.models.MediaData
import com.example.projectorcasting.prefrences.AppPreference
import com.example.projectorcasting.utils.AppConstants
import com.example.projectorcasting.utils.AppUtils
import com.example.projectorcasting.utils.MediaListSingleton
import com.example.projectorcasting.utils.PromptHelper
import com.example.projectorcasting.viewmodels.AudioViewModel
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.cast.framework.CastState
import com.quantum.projector.screenmirroring.cast.casting.phoneprojector.videoprojector.casttv.castforchromecast.screencast.casttotv.R
import com.quantum.projector.screenmirroring.cast.casting.phoneprojector.videoprojector.casttv.castforchromecast.screencast.casttotv.databinding.FragmentAudioBinding
import engine.app.adshandler.AHandler
import engine.app.analytics.logGAEvents
import engine.app.listener.OnRewardedEarnedItem
import engine.app.server.v2.Slave
import io.github.dkbai.tinyhttpd.nanohttpd.core.util.PathSingleton
import java.io.File

class AudioFragment : BaseFragment(R.layout.fragment_audio) {

    private val audioViewModel: AudioViewModel by viewModels()

    private var binding: FragmentAudioBinding? = null
    private var audioAdapter: AudioAdapter? = null
    private var isCastConnected = false
    private var itemMediaData: MediaData? = null
    private var mediaMapList: ArrayList<MediaData>? = null
    private var appPreference: AppPreference? = null
    private var isRewardedCompleted = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAudioBinding.bind(view)
        appPreference = context?.let { AppPreference(it) }

        observeCastingLiveData()
        observeAudioList()
        doFetchingWork()

        getConnectionStatus()

        binding?.llSorting?.setOnClickListener {
            logGAEvents(AnalyticsConstant.GA_Audio_Date_Sorting)
            sortMediaList()
        }

        binding?.llConnect?.setOnClickListener {
            logGAEvents(AnalyticsConstant.GA_Audio_Cast_Connect)
//            openScanDevicePage()
            PromptHelper.showCastingPrompt(
                context,
                ::castPromtAction,
                isCastConnected,
                itemMediaData
            )
        }

        binding?.llConnected?.setOnClickListener {
            logGAEvents(AnalyticsConstant.GA_Audio_Cast_DisConnect)
            audioViewModel.showConnectionPrompt(context, ::actionPerform, false, null)
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

        binding?.tvQueued?.setOnClickListener {
            logGAEvents(AnalyticsConstant.GA_Audio_Queue_Button)
            findNavController().navigate(R.id.nav_queue)
            showNavigationFullAds(activity)
        }

        val provider:QueueDataProvider? = QueueDataProvider.getInstance(context)
        if (appPreference?.isImageCasting() == true && provider?.count!! >0)
            provider.removeAll()

        if (provider?.count!! > 0) {
            binding?.tvQueued?.visibility = View.VISIBLE
        } else
            binding?.tvQueued?.visibility = View.GONE

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
            binding?.rlSorting?.visibility = View.VISIBLE
            binding?.rvAudio?.visibility = View.VISIBLE
            binding?.tvNoAudiosFound?.visibility = View.GONE
            audioAdapter?.refreshList(mediaMapList)
            return
        } else {
            binding?.rlSorting?.visibility = View.GONE
        }

        var filteredDataList: ArrayList<MediaData>? = arrayListOf()

        for (data in mediaMapList!!) {
            if (data.file?.name?.lowercase()?.trim()?.contains(text.lowercase().trim()) == true) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredDataList?.add(data)
            }
        }

        if (filteredDataList?.isEmpty() == true) {
            // if no item is added in filtered list we are
            // displaying a message as no data found.
            binding?.rvAudio?.visibility = View.GONE
            binding?.tvNoAudiosFound?.visibility = View.VISIBLE
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            binding?.rvAudio?.visibility = View.VISIBLE
            binding?.tvNoAudiosFound?.visibility = View.GONE
            audioAdapter?.filtereList(filteredDataList)
        }

    }

    private fun openScanDevicePage() {
        findNavController().navigate(R.id.nav_scan_device)
        showFullAds(activity)
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


    private fun observeAudioList() {
        getDashViewModel()?.audiosList?.observe(viewLifecycleOwner, Observer { audioList ->
            hideLoader()
            setAdapter(audioList)
        })
    }

    private fun setAdapter(audioList: List<MediaData>) {
        mediaMapList = ArrayList(audioList)
        if (audioList.isNotEmpty()) {
            binding?.rlView?.visibility = View.VISIBLE
            binding?.tvNoAudiosFound?.visibility = View.GONE

            binding?.rvAudio?.layoutManager = LinearLayoutManager(context)
            audioAdapter = AudioAdapter(::itemClick)
            binding?.rvAudio?.adapter = audioAdapter
            audioAdapter?.refreshList(audioList)

        } else {
            binding?.rlView?.visibility = View.GONE
            binding?.tvNoAudiosFound?.visibility = View.VISIBLE
        }
    }

    private fun doFetchingWork() {
        val pathList = MediaListSingleton.getGalleryAudioList()
        if (pathList != null && pathList.isNotEmpty()) {
            setAdapter(pathList)
        } else {
            if (getDashViewModel()?.isLoading == true)
                showLoader()
            else {
                showLoader()
                context?.let { getDashViewModel()?.fetchAudioList(it) }
            }
        }
    }

    private fun itemClick(mediaData: MediaData) {
        appPreference?.setImageCasting(false)
        itemMediaData = mediaData
        if (isCastConnected) {
            val thumb = File(
                AppUtils.createAudioThumbPath(context),
                AppConstants.AUDIO_THUMB
            ).path.split("0/")[1]
            val path = mediaData.file?.path?.split("0/")?.get(1)
            path?.let {
                CastHelper.playMedia(
                    context,
                    mediaData,
                    it,
                    thumb,
                    Utils.AUDIO,
                    ::checkForQueue,
                    getConnectedDeviceName()
                )
            }

            //set path for html
            showAudioInHtml(mediaData)
        } else {
            PromptHelper.showCastingPrompt(context, ::castPromtAction, isCastConnected, mediaData)
        }
    }

    private fun showAudioInHtml(mediaData: MediaData?) {
        val path = mediaData?.file?.path?.split("0/")?.get(1)
        var pathList: java.util.ArrayList<String> = arrayListOf()
        pathList.add(path.toString())
        PathSingleton.setAudioPath(pathList)
        PathSingleton.setImagePath(null)
        PathSingleton.setVideoPath(null)
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

        audioAdapter?.refreshList(audioAdapter?.getAudioList()?.reversed())
    }

    private fun actionPerform(isConnect: Boolean, castModel: CastModel?) {
        if (isConnect)
            startCasting(castModel?.routeInfo, castModel?.castDevice)
        else
            stopCasting()
    }

    private fun checkForQueue(count: Int, openBrowser: Boolean, openDeviceListPage: Boolean) {
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
            AHandler.getInstance().showRewardedVideoOrFullAds(activity, true, object :
                OnRewardedEarnedItem {
                override fun onRewardedLoaded() {

                }

                override fun onRewardedFailed(msg: String?) {
                    openPageForConnection(isCastDeviceClick, mediaData)
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
            showAudioInHtml(mediaData)
            openBrowserPage()
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