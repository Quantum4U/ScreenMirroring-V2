package com.example.projectorcasting.ui.fragments

import android.os.Bundle
import android.view.View
import com.quantum.projector.screenmirroring.cast.casting.phoneprojector.videoprojector.casttv.castforchromecast.screencast.casttotv.R
import com.quantum.projector.screenmirroring.cast.casting.phoneprojector.videoprojector.casttv.castforchromecast.screencast.casttotv.databinding.FragmentCompletionPageBinding

class CompletionPageFragment : BaseFragment(R.layout.fragment_completion_page) {

    private var binding: FragmentCompletionPageBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCompletionPageBinding.bind(view)
    }
}