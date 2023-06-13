package com.example.projectorcasting.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.example.projectorcasting.R
import com.example.projectorcasting.adapter.ImagePreviewAdapter
import com.example.projectorcasting.casting.model.CastModel
import com.example.projectorcasting.casting.utils.CastHelper
import com.example.projectorcasting.databinding.FragmentImagePreviewBinding
import com.example.projectorcasting.utils.MediaListSingleton
import com.google.android.gms.cast.framework.CastState

class ImagePreviewFragment : BaseFragment(R.layout.fragment_image_preview) {

    private var binding: FragmentImagePreviewBinding? = null
    private var imagePreviewAdapter: ImagePreviewAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentImagePreviewBinding.bind(view)

        observeCastingLiveData()

//        imagePreviewAdapter = ImagePreviewAdapter(MediaListSingleton.getGalleryImageList())
        binding?.vpImgPreview?.adapter = imagePreviewAdapter

        binding?.vpImgPreview?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
//                CastHelper.castPhotos(getRemoteMediaClient())
            }

        })


        binding?.llConnect?.setOnClickListener {
            findNavController().navigate(R.id.nav_scan_device)
        }

        binding?.llConnected?.setOnClickListener {
            getDashViewModel()?.showConnectionPrompt(context, ::actionPerform, false, null)
        }

        binding?.ivBack?.setOnClickListener {
            exitPage()
        }

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

    private fun actionPerform(isConnect: Boolean, castModel: CastModel?) {
        if (isConnect)
            startCasting(castModel?.routeInfo, castModel?.castDevice)
        else
            stopCasting()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun exitPage() {
        findNavController().navigateUp()
    }
}