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
import com.example.projectorcasting.R
import com.example.projectorcasting.adapter.VideoHorizontalAdapter
import com.example.projectorcasting.adapter.VideoSectionalAdapter
import com.example.projectorcasting.casting.model.CastModel
import com.example.projectorcasting.casting.queue.QueueDataProvider
import com.example.projectorcasting.casting.utils.CastHelper
import com.example.projectorcasting.casting.utils.Utils
import com.example.projectorcasting.databinding.FragmentVideosBinding
import com.example.projectorcasting.models.MediaData
import com.example.projectorcasting.utils.AppConstants
import com.example.projectorcasting.utils.MediaListSingleton
import com.example.projectorcasting.viewmodels.VideoViewModel
import com.google.android.gms.cast.framework.CastState

class VideosFragment : BaseFragment(R.layout.fragment_videos){

    private val videoViewModel: VideoViewModel by viewModels()
    private var binding: FragmentVideosBinding? = null

    private var videoAdapter: VideoHorizontalAdapter? = null
    private var videoSectionalAdapter: VideoSectionalAdapter? = null
    private var mediaMapList: HashMap<String, List<MediaData>>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentVideosBinding.bind(view)

        observeCastingLiveData()
        observeVideoList()
        doFetchingWork()

        binding?.llSorting?.setOnClickListener {
            sortMediaList()
        }

        binding?.llConnect?.setOnClickListener {
            findNavController().navigate(R.id.nav_scan_device)
        }

        binding?.llConnected?.setOnClickListener {
            videoViewModel.showConnectionPrompt(context, ::actionPerform, false, null)
        }

        binding?.ivBack?.setOnClickListener {
            exitPage()
        }

        binding?.tvQueued?.setOnClickListener {
            findNavController().navigate(R.id.nav_queue)
        }

        val provider: QueueDataProvider? = QueueDataProvider.Companion.getInstance(context)
        if (provider?.count!! > 0)
            binding?.tvQueued?.visibility = View.VISIBLE
        else
            binding?.tvQueued?.visibility = View.GONE

    }

    private fun doFetchingWork() {
        showLoader()
        context?.let { videoViewModel.fetchVideoList(it) }
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

    private fun observeVideoList() {
        videoViewModel.videosList.observe(viewLifecycleOwner, Observer { videoList ->
            hideLoader()
            if (videoList != null && videoList.isNotEmpty()) {

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
        })
    }

    private fun setSectionAdapter() {
        mediaMapList = MediaListSingleton.getGalleryVideoList()
        videoSectionalAdapter =
            VideoSectionalAdapter(requireContext(), mediaMapList, ::itemClick)
        val layoutManager = GridLayoutManager(requireContext(), 1)
        videoSectionalAdapter?.setLayoutManager(layoutManager)
        videoSectionalAdapter?.shouldShowHeadersForEmptySections(true)
        binding?.rvVertical?.hasFixedSize()
        binding?.rvVertical?.layoutManager = layoutManager
        binding?.rvVertical?.adapter = videoSectionalAdapter
    }

    private fun itemClick(mediaData: MediaData) {
        val path = mediaData.file?.path?.split("0/")?.get(1)
        path?.let { CastHelper.playMedia(context, mediaData.file, it, "", Utils.VIDEO,::checkForQueue) }
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

        videoSectionalAdapter?.refreshList(mediaMapList)
    }

    private fun actionPerform(isConnect: Boolean, castModel: CastModel?) {
        if (isConnect)
            startCasting(castModel?.routeInfo, castModel?.castDevice)
        else
            stopCasting()
    }

    private fun checkForQueue(count: Int){
        Log.d("VideosFragment", "onViewCreated A13 : ><<<"+count)
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