package com.example.projectorcasting.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.quantum.projector.screenmirroring.cast.casting.phoneprojector.videoprojector.casttv.castforchromecast.screencast.casttotv.R
import com.quantum.projector.screenmirroring.cast.casting.phoneprojector.videoprojector.casttv.castforchromecast.screencast.casttotv.databinding.FragmentWebviewBinding

class WebViewFragment : BaseFragment(R.layout.fragment_webview) {

    private var binding: FragmentWebviewBinding? = null
    private val argument: WebViewFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentWebviewBinding.bind(view)

        binding?.webView?.settings?.javaScriptEnabled = true
        binding?.webView?.loadUrl(argument.url)
    }
}