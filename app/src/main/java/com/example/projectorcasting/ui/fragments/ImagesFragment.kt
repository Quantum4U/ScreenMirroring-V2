package com.example.projectorcasting.ui.fragments

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.projectorcasting.R
import com.example.projectorcasting.adapter.ImageSectionalAdapter
import com.example.projectorcasting.adapter.VideoSectionalAdapter
import com.example.projectorcasting.databinding.FragmentImagesBinding
import com.example.projectorcasting.models.MediaData
import com.example.projectorcasting.utils.AppUtils
import com.example.projectorcasting.utils.MediaListSingleton
import com.example.projectorcasting.utils.SpacesItemDecoration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImagesFragment : BaseFragment(R.layout.fragment_images) {

    private var binding: FragmentImagesBinding? = null
    private var imageSectionalAdapter:ImageSectionalAdapter?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentImagesBinding.bind(view)
        observeImageList()

        doFetchingWork()
    }

    private fun doFetchingWork(){
        showLoader()
        getDashViewModel()?.fetchImages(context)
    }

    private fun observeImageList(){
        getDashViewModel()?.imagesList?.observe(viewLifecycleOwner, Observer { imageList ->
            hideLoader()
            if(imageList != null && imageList.isNotEmpty()){
                imageSectionalAdapter =
                    ImageSectionalAdapter(requireContext(), MediaListSingleton.getGalleryImageHashMap(), ::itemClick)
                val layoutManager = GridLayoutManager(requireContext(), 3)
                imageSectionalAdapter?.setLayoutManager(layoutManager)
                imageSectionalAdapter?.shouldShowHeadersForEmptySections(true)
                binding?.rvImages?.hasFixedSize()
                binding?.rvImages?.layoutManager = layoutManager
                binding?.rvImages?.adapter = imageSectionalAdapter
//                val space = TypedValue.applyDimension(
//                    TypedValue.COMPLEX_UNIT_DIP, 90f,
//                    requireActivity().resources.displayMetrics
//                ).toInt()
                binding?.rvImages?.addItemDecoration(SpacesItemDecoration(1))
            }
        })
    }

    private fun itemClick(mediaData: MediaData) {

    }
}