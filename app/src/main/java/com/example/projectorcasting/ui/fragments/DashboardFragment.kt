package com.example.projectorcasting.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.projectorcasting.R
import com.example.projectorcasting.databinding.FragmentDashboardBinding
import com.example.projectorcasting.ui.activities.MainActivity
import com.example.projectorcasting.viewmodels.DashboardViewModel
import com.google.android.gms.cast.framework.CastState

class DashboardFragment : BaseFragment(R.layout.fragment_dashboard) {

    private val viewModel: DashboardViewModel by viewModels()
    private var binding: FragmentDashboardBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentDashboardBinding.bind(view)

        observeCastingLiveData()

        binding?.ivNavIcon?.setOnClickListener {
            (activity as MainActivity?)?.openDrawer()
        }

        binding?.llStartMirrioring?.setOnClickListener {
            startMirroring()
        }

        binding?.cvCastVideos?.setOnClickListener {
            findNavController().navigate(R.id.nav_video)
        }

    }

    private fun observeCastingLiveData(){
        castingLiveData().observe(viewLifecycleOwner, Observer { state ->
            if (state == CastState.CONNECTED) {

            }else if (state == CastState.NOT_CONNECTED){

            }

        })    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}