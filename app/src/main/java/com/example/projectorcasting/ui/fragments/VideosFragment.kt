package com.example.projectorcasting.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.example.projectorcasting.utils.AppConstants
import com.example.projectorcasting.utils.AppUtils
import com.example.projectorcasting.utils.MediaListSingleton
import com.example.projectorcasting.utils.PromptHelper
import com.example.projectorcasting.viewmodels.VideoViewModel
import com.google.android.gms.cast.framework.CastState
import com.quantum.projector.screenmirroring.cast.casting.phoneprojector.videoprojector.casttv.castforchromecast.screencast.casttotv.R
import com.quantum.projector.screenmirroring.cast.casting.phoneprojector.videoprojector.casttv.castforchromecast.screencast.casttotv.databinding.FragmentVideosBinding
import engine.app.analytics.logGAEvents
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
    private var itemMediaData:MediaData?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentVideosBinding.bind(view)

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
            PromptHelper.showCastingPrompt(context, ::castPromtAction, isCastConnected, itemMediaData)
        }

        binding?.llConnected?.setOnClickListener {
            logGAEvents(AnalyticsConstant.GA_Videos_Cast_DisConnect)
            videoViewModel.showConnectionPrompt(context, ::actionPerform, false, null)
        }

        binding?.ivBack?.setOnClickListener {
            exitPage()
        }

        binding?.tvQueued?.setOnClickListener {
            logGAEvents(AnalyticsConstant.GA_Videos_Queue_Button)
            findNavController().navigate(R.id.nav_queue)
            showFullAds(activity)
        }

        val provider: QueueDataProvider? = QueueDataProvider.Companion.getInstance(context)
        if (provider?.count!! > 0)
            binding?.tvQueued?.visibility = View.VISIBLE
        else
            binding?.tvQueued?.visibility = View.GONE

        setBrowserValue()
    }

    private fun openScanDevicePage(){
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
                binding?.ivCasting?.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_cast_enable,null))
            } else if (state == CastState.NOT_CONNECTED) {
                isCastConnected = false
//                binding?.llConnected?.visibility = View.GONE
//                binding?.llConnect?.visibility = View.VISIBLE
                binding?.ivCasting?.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_cast_disable,null))
            }

            setBrowserValue()

        })
    }

    private fun getConnectionStatus(){
        isCastConnected = isCastingConnected() == true
        if(isCastConnected)
            binding?.ivCasting?.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_cast_enable,null))
        else
            binding?.ivCasting?.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_cast_disable,null))
    }

    private fun setBrowserValue(){
        if(isServerRunning()){
            binding?.ivBrowser?.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_browser_enable,null))
        }else{
            binding?.ivBrowser?.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_browser_disable,null))
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
        val thumbPath = thumbFile?.path?.split("0/")?.get(1)
        val path = mediaData?.file?.path?.split("0/")?.get(1)
        path?.let {
            CastHelper.playMedia(
                context, mediaData, it,
                thumbPath.toString(), Utils.VIDEO, ::checkForQueue,getConnectedDeviceName()
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

    private fun castPromtAction(isCastDeviceClick: Boolean,mediaData: MediaData?) {
        if (isCastDeviceClick) {
//            if (!isCastConnected)
                findNavController().navigate(R.id.nav_scan_device)
//            else
//                stopCasting()
        } else {
            showVideoInHtml(mediaData)
            openBrowserPage()
        }
    }

    private fun checkForQueue(count: Int,openBrowser:Boolean,openDeviceListPage:Boolean) {
        Log.d("VideosFragment", "onViewCreated A13 : ><<<" + count)
        if(count > 0) {
            binding?.tvQueued?.visibility = View.VISIBLE
        }

        if(openBrowser){
            openBrowserPage()
        }

        if (openDeviceListPage){
            openScanDevicePage()
        }

    }

    private fun openBrowserPage(){
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