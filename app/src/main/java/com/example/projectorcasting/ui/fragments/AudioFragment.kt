package com.example.projectorcasting.ui.fragments

import android.os.Bundle
import android.view.View
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
import com.example.projectorcasting.utils.AppConstants
import com.example.projectorcasting.utils.AppUtils
import com.example.projectorcasting.utils.MediaListSingleton
import com.example.projectorcasting.utils.PromptHelper
import com.example.projectorcasting.viewmodels.AudioViewModel
import com.google.android.gms.cast.framework.CastState
import com.quantum.projector.screenmirroring.cast.casting.phoneprojector.videoprojector.casttv.castforchromecast.screencast.casttotv.R
import com.quantum.projector.screenmirroring.cast.casting.phoneprojector.videoprojector.casttv.castforchromecast.screencast.casttotv.databinding.FragmentAudioBinding
import engine.app.analytics.logGAEvents
import io.github.dkbai.tinyhttpd.nanohttpd.core.util.PathSingleton
import java.io.File

class AudioFragment : BaseFragment(R.layout.fragment_audio) {

    private val audioViewModel: AudioViewModel by viewModels()

    private var binding: FragmentAudioBinding? = null
    private var audioAdapter: AudioAdapter? = null
    private var isCastConnected = false
    private var itemMediaData: MediaData? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAudioBinding.bind(view)

        observeCastingLiveData()
        observeAudioList()
        doFetchingWork()

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
            exitPage()
        }

        binding?.tvQueued?.setOnClickListener {
            logGAEvents(AnalyticsConstant.GA_Audio_Queue_Button)
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

    private fun setBrowserValue() {
        if (getServerValue() == true) {
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
        if (isCastDeviceClick) {
//            if (!isCastConnected)
                findNavController().navigate(R.id.nav_scan_device)
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