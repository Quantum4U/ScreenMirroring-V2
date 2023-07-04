package com.example.projectorcasting.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.projectorcasting.AnalyticsConstant
import com.example.projectorcasting.casting.model.CastModel
import com.example.projectorcasting.casting.utils.CastHelper
import com.example.projectorcasting.ui.activities.MainActivity
import com.example.projectorcasting.utils.AppUtils
import com.example.projectorcasting.viewmodels.DashboardViewModel
import com.google.android.gms.cast.CastDevice
import com.google.android.gms.cast.framework.CastState
import com.quantum.projector.screenmirroring.cast.casting.phoneprojector.videoprojector.casttv.castforchromecast.screencast.casttotv.R
import com.quantum.projector.screenmirroring.cast.casting.phoneprojector.videoprojector.casttv.castforchromecast.screencast.casttotv.databinding.FragmentDashboardBinding
import engine.app.adshandler.AHandler
import engine.app.analytics.logGAEvents
import io.github.dkbai.tinyhttpd.nanohttpd.core.util.ServerConstants

class DashboardFragment : BaseFragment(R.layout.fragment_dashboard) {

    private val viewModel: DashboardViewModel by viewModels()
    private var binding: FragmentDashboardBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentDashboardBinding.bind(view)

        observeCastingLiveData()

        getConnectionStatus()

        binding?.ivNavIcon?.setOnClickListener {
            (activity as MainActivity?)?.openDrawer()
        }

        binding?.llStartMirrioring?.setOnClickListener {
            logGAEvents(AnalyticsConstant.GA_Dashboard_Mirroring)
            startMirroring()
        }

        binding?.rlCast?.setOnClickListener {
            logGAEvents(AnalyticsConstant.GA_Dashboard_Cast_Connect)
            findNavController().navigate(R.id.nav_scan_device)
            showFullAds(activity)
        }

        binding?.cvCastPhotos?.setOnClickListener {
            logGAEvents(AnalyticsConstant.GA_Dashboard_Photos)
            (activity as MainActivity?)?.openImagePage()
        }

        binding?.cvCastVideos?.setOnClickListener {
            logGAEvents(AnalyticsConstant.GA_Dashboard_Videos)
            (activity as MainActivity?)?.openVideoPage()
        }

        binding?.cvCastAudios?.setOnClickListener {
            logGAEvents(AnalyticsConstant.GA_Dashboard_Audios)
            (activity as MainActivity?)?.openAudioPage()
        }

        binding?.cvPro?.setOnClickListener {
            logGAEvents(AnalyticsConstant.GA_Dashboard_GoPro)
            AHandler.getInstance().showRemoveAdsPrompt(context)
        }

        binding?.tvDisconnect?.setOnClickListener {
            logGAEvents(AnalyticsConstant.GA_Dashboard_Cast_DisConnect)
            getDashViewModel()?.showConnectionPrompt(context, ::actionPerform, false, null)
        }

        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    (activity as MainActivity?)?.closeDrawer()
                }
            })

        binding?.rlBottomLayout?.setOnClickListener {
            openBrowserPage()
        }

        binding?.tvShareLink?.setOnClickListener {
            val url =
                "http://${CastHelper.deviceIpAddress}:${ServerConstants.PORT_VALUE}/${ServerConstants.URL_KEYWORD}/"
            AppUtils.shareUrl(context, url)
        }

        binding?.tvBottomDisconnect?.setOnClickListener {
            stopServer()
            manageBrowserLayout()
        }

        manageBrowserLayout()
    }

    private fun openBrowserPage() {
        findNavController().navigate(R.id.nav_browse_cast)
        showFullAds(activity)
    }

    private fun manageBrowserLayout() {
        if (getServerValue() == true) {
            binding?.rlBottomLayout?.visibility = View.VISIBLE
        } else {
            binding?.rlBottomLayout?.visibility = View.GONE
        }
    }

    private fun observeCastingLiveData() {
        castingLiveData().observe(viewLifecycleOwner, Observer { state ->
            if (state == CastState.CONNECTED) {
                checkCastConnection(true, getConnectedDeviceName())
            } else if (state == CastState.NOT_CONNECTED) {
                checkCastConnection(false, getConnectedDeviceName())
            }

        })
    }

    private fun getConnectionStatus() {
        val isConnected = isCastingConnected()
        Log.d("TAG", "getConnectionStatus: >>" + isConnected)
        if (isConnected != null) {
            checkCastConnection(isConnected, getConnectedDeviceName())
        } else {
            Log.d("TAG", "getConnectionStatus: >>00" + getMediaRouter())
            getMediaRouter()?.let { router ->
                CastHelper.getCastEnabled(
                    router,
                    ::castingEnabledCallback
                )
            }
        }
    }

    private fun checkCastConnection(isConnected: Boolean, deviceName: String?) {
        if (isConnected) {
            binding?.tvConnectedDeviceName?.text = getString(R.string.connected, deviceName)

            binding?.llConnectedDeviceName?.visibility = View.VISIBLE
            binding?.tvDisconnect?.visibility = View.VISIBLE
            binding?.tvNoDeviceConnected?.visibility = View.GONE
        } else {
            binding?.llConnectedDeviceName?.visibility = View.INVISIBLE
            binding?.tvDisconnect?.visibility = View.GONE
            binding?.tvNoDeviceConnected?.visibility = View.VISIBLE

            //delete videos thumbs created for casting media if it presents
            getDashViewModel()?.deleteTempThumbFolder(context)
        }
    }

    private fun castingEnabledCallback(isConnected: Boolean, device: CastDevice?) {

        Log.d("TAG", "getConnectionStatus: >>11" + device)
        val deviceName: String? = device?.modelName
        setConnectionInfo(isConnected, device)

        Log.d("TAG", "getConnectionStatus: >>22" + isConnected + "//" + deviceName)
        checkCastConnection(isConnected, deviceName)
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
}