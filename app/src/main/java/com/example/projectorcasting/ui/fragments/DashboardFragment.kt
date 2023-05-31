package com.example.projectorcasting.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.example.projectorcasting.R
import com.example.projectorcasting.databinding.FragmentDashboardBinding
import com.example.projectorcasting.ui.activities.MainActivity
import com.example.projectorcasting.viewmodels.DashboardViewModel

class DashboardFragment : BaseFragment(R.layout.fragment_dashboard) {

    private val viewModel: DashboardViewModel by viewModels()
    private var binding: FragmentDashboardBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentDashboardBinding.bind(view)

        binding?.ivNavIcon?.setOnClickListener {
            (activity as MainActivity?)?.openDrawer()
        }

        binding?.llStartMirrioring?.setOnClickListener {
            startMirroring()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}