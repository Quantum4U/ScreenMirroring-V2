package com.example.projectorcasting.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.projectorcasting.R
import com.example.projectorcasting.casting.utils.CastHelper
import com.example.projectorcasting.databinding.FragmentVideosBinding
import com.google.android.gms.cast.framework.CastState

class VideosFragment : BaseFragment(R.layout.fragment_videos) {

    private var binding: FragmentVideosBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentVideosBinding.bind(view)

        observeCastingLiveData()

        binding?.play?.setOnClickListener {
            val list = getMediaRouter()?.let { it1 -> CastHelper.getAvailableDevices(it1) }
            startCasting(list?.get(0)?.routeInfo!!, list[0].castDevice!!)
        }

        binding?.etPath?.setOnClickListener {
            stopCasting()
        }

    }

    private fun observeCastingLiveData(){
        castingLiveData().observe(viewLifecycleOwner, Observer { state ->
            if (state == CastState.CONNECTED) {
                Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show()
            }else if (state == CastState.NOT_CONNECTED){
                Toast.makeText(context, "DisConnected", Toast.LENGTH_SHORT).show()
            }

        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}