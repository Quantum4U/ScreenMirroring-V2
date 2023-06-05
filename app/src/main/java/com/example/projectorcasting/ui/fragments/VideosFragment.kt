package com.example.projectorcasting.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectorcasting.R
import com.example.projectorcasting.adapter.VideoHorizontalAdapter
import com.example.projectorcasting.databinding.FragmentVideosBinding
import com.example.projectorcasting.models.MediaData
import com.example.projectorcasting.utils.AppConstants
import com.example.projectorcasting.viewmodels.VideoViewModel
import com.google.android.gms.cast.framework.CastState

class VideosFragment : BaseFragment(R.layout.fragment_videos) {

    private val videoViewModel: VideoViewModel by viewModels()
    private var binding: FragmentVideosBinding? = null

    private var videoAdapter: VideoHorizontalAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentVideosBinding.bind(view)

        observeCastingLiveData()
        observeVideoList()
        doFetchingWork()

        binding?.llSorting?.setOnClickListener {
            sortMediaList()
        }
    }

    private fun doFetchingWork() {
        showLoader()
        context?.let { videoViewModel.fetchVideoList(it) }
    }

    private fun observeCastingLiveData() {
        castingLiveData().observe(viewLifecycleOwner, Observer { state ->
            if (state == CastState.CONNECTED) {
                Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show()
            } else if (state == CastState.NOT_CONNECTED) {
                Toast.makeText(context, "DisConnected", Toast.LENGTH_SHORT).show()
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
                }

                var layoutManagerTopProducts = object : LinearLayoutManager(requireContext()) {

                }

                layoutManagerTopProducts.orientation = LinearLayoutManager.HORIZONTAL
                binding?.rvHorizontal?.layoutManager = layoutManagerTopProducts
//                binding?.rvHorizontal?.setHasFixedSize(true)
//                binding?.rvHorizontal?.setItemViewCacheSize(10)
                videoAdapter = VideoHorizontalAdapter(videoList, ::itemClick)
                binding?.rvHorizontal?.adapter = videoAdapter
            } else {
                binding?.rlView?.visibility = View.GONE
                binding?.tvNoVideosFound?.visibility = View.VISIBLE
            }
        })
    }

    private fun itemClick(boolean: Boolean, mediaData: MediaData) {

    }

    private fun sortMediaList(){
        if(binding?.tvSortingText?.text?.equals(getString(R.string.ascending)) == true){
            binding?.ivSortingIcon?.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_descending_icon,null))
            binding?.tvSortingText?.text = getString(R.string.descending)
        }else{
            binding?.ivSortingIcon?.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_ascending_icon,null))
            binding?.tvSortingText?.text = getString(R.string.ascending)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}