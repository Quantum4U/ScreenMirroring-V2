package com.example.projectorcasting.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectorcasting.AnalyticsConstant
import com.quantum.projector.screenmirroring.cast.casting.phoneprojector.videoprojector.casttv.castforchromecast.screencast.casttotv.R
import com.example.projectorcasting.adapter.AudioAdapter
import com.example.projectorcasting.casting.model.CastModel
import com.example.projectorcasting.casting.queue.QueueDataProvider
import com.example.projectorcasting.casting.utils.CastHelper
import com.example.projectorcasting.casting.utils.Utils
import com.quantum.projector.screenmirroring.cast.casting.phoneprojector.videoprojector.casttv.castforchromecast.screencast.casttotv.databinding.FragmentAudioBinding
import com.example.projectorcasting.models.MediaData
import com.example.projectorcasting.utils.AppConstants
import com.example.projectorcasting.utils.AppUtils
import com.example.projectorcasting.utils.MediaListSingleton
import com.example.projectorcasting.viewmodels.AudioViewModel
import com.google.android.gms.cast.framework.CastState
import engine.app.analytics.logGAEvents
import java.io.File

class AudioFragment : BaseFragment(R.layout.fragment_audio) {

    private val audioViewModel: AudioViewModel by viewModels()

    private var binding: FragmentAudioBinding? = null
    private var audioAdapter: AudioAdapter? = null

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
            findNavController().navigate(R.id.nav_scan_device)
            showFullAds(activity)
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

    }

    private fun observeCastingLiveData() {
        castingLiveData().observe(viewLifecycleOwner, Observer { state ->
            if (state == CastState.CONNECTED) {
                binding?.llConnected?.visibility = View.VISIBLE
                binding?.llConnect?.visibility = View.GONE
                binding?.tvConnected?.text = getString(R.string.connected, getConnectedDeviceName())
            } else if (state == CastState.NOT_CONNECTED) {
                binding?.llConnected?.visibility = View.GONE
                binding?.llConnect?.visibility = View.VISIBLE
            }
        })
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
        if(pathList != null && pathList.isNotEmpty()) {
            setAdapter(pathList)
        }else{
            if (getDashViewModel()?.isLoading == true)
                showLoader()
            else {
                showLoader()
                context?.let { getDashViewModel()?.fetchAudioList(it) }
            }
        }
    }

    private fun itemClick(mediaData: MediaData) {
        val thumb = File(AppUtils.createAudioThumbPath(context), AppConstants.AUDIO_THUMB).path.split("0/")[1]
        val path = mediaData.file?.path?.split("0/")?.get(1)
        path?.let {
            CastHelper.playMedia(context, mediaData, it, thumb, Utils.AUDIO, ::checkForQueue)
        }
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

    private fun checkForQueue(count: Int) {
        binding?.tvQueued?.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun exitPage() {
        findNavController().navigateUp()
    }
}