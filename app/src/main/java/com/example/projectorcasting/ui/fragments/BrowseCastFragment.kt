package com.example.projectorcasting.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectorcasting.adapter.BrowserAdapter
import com.example.projectorcasting.casting.utils.CastHelper
import com.example.projectorcasting.models.BrowserModel
import com.example.projectorcasting.utils.AppUtils
import com.quantum.projector.screenmirroring.cast.casting.phoneprojector.videoprojector.casttv.castforchromecast.screencast.casttotv.R
import com.quantum.projector.screenmirroring.cast.casting.phoneprojector.videoprojector.casttv.castforchromecast.screencast.casttotv.databinding.FragmentBrowseCastBinding
import io.github.dkbai.tinyhttpd.nanohttpd.core.util.ServerConstants

class BrowseCastFragment : BaseFragment(R.layout.fragment_browse_cast) {

    private var binding: FragmentBrowseCastBinding? = null
    private var adapter: BrowserAdapter? = null
    private var itemList: ArrayList<BrowserModel> = arrayListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentBrowseCastBinding.bind(view)

        //start server
        CastHelper.startServer(context)

        setServerValue(true)

        itemList.add(
            BrowserModel(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_browse_icon1,
                    null
                ), getString(R.string.browser_text1)
            )
        )
        itemList.add(
            BrowserModel(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_browse_icon2,
                    null
                ), getString(R.string.browser_text2)
            )
        )

        binding?.recyclerview?.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL, false
        )

        adapter = BrowserAdapter(itemList)
        binding?.recyclerview?.adapter = adapter
        binding?.circleIndicator?.setRecyclerView(binding?.recyclerview)

        val url = "http://${CastHelper.deviceIpAddress}:${ServerConstants.PORT_VALUE}/${ServerConstants.URL_KEYWORD}/"
        binding?.tvUrl?.text = url

        binding?.ivShare?.setOnClickListener {
            AppUtils.shareUrl(context, binding?.tvUrl?.text.toString())
        }

        binding?.tvDisconnect?.setOnClickListener {
            showToast(getString(R.string.connection_closed))
            stopServer()
        }

        binding?.tvPreview?.setOnClickListener {
            val action =
                BrowseCastFragmentDirections.actionBrowseToPreview(url)
            findNavController().navigate(action)
        }

        binding?.ivBack?.setOnClickListener {
            exitPage()
        }
    }

    private fun exitPage() {
        findNavController().navigateUp()
    }
}