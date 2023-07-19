package com.example.projectorcasting.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectorcasting.adapter.ScanDeviceAdapter
import com.example.projectorcasting.casting.model.CastModel
import com.example.projectorcasting.casting.utils.CastHelper
import com.example.projectorcasting.utils.AppConstants
import com.example.projectorcasting.viewmodels.ScanViewModel
import com.google.android.gms.cast.CastDevice
import com.google.android.gms.cast.framework.CastState
import com.quantum.projector.screenmirroring.cast.casting.phoneprojector.videoprojector.casttv.castforchromecast.screencast.casttotv.R
import com.quantum.projector.screenmirroring.cast.casting.phoneprojector.videoprojector.casttv.castforchromecast.screencast.casttotv.databinding.FragmentScandeviceBinding
import engine.app.serviceprovider.Utils

class ScanDeviceFragment : BaseFragment(R.layout.fragment_scandevice) {

    private val scanViewModel: ScanViewModel by viewModels()
    private var binding: FragmentScandeviceBinding? = null
    private var scanDeviceAdapter: ScanDeviceAdapter? = null
    private var fromSlideShow = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentScandeviceBinding.bind(view)
        observeCastingLiveData()
        observeDeviceList()

        fromSlideShow = arguments?.getBoolean(AppConstants.FOR_START_SLIDESHOW) == true

        binding?.ivBack?.setOnClickListener {
            exitPage()
        }

        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    exitPage()
                }
            })

        binding?.ivRefresh?.setOnClickListener {
            Log.d("ScanDeviceFragment", "onViewCreated A13 : <<<<00")
            checkWifiNetwork()
        }

        binding?.tvOpenWifi?.setOnClickListener {
            openWifi()
        }

        checkWifiNetwork()
//        showNativeMedium(binding?.bottomBannerAd, activity)
    }

    private fun observeDeviceList() {
        scanViewModel.deviceList.observe(viewLifecycleOwner, Observer { list ->
//            hideLoader()
            hideProgress()
            if (list != null && list.isNotEmpty()) {
                binding?.llItemLayout?.visibility = View.VISIBLE
                binding?.llNoDeviceFound?.visibility = View.GONE
                binding?.llScanning?.visibility = View.GONE
                binding?.llNoNetwork?.visibility = View.GONE

                scanDeviceAdapter = ScanDeviceAdapter(list, ::itemClick)
                binding?.rvScanDevice?.layoutManager = LinearLayoutManager(context)
                binding?.rvScanDevice?.adapter = scanDeviceAdapter
                getConnectionStatus()
            } else {
                binding?.llNoDeviceFound?.visibility = View.VISIBLE
                binding?.llScanning?.visibility = View.GONE
                binding?.llNoNetwork?.visibility = View.GONE
                binding?.llItemLayout?.visibility = View.GONE
            }
        })
    }

    private fun observeCastingLiveData() {
        castingLiveData().observe(viewLifecycleOwner, Observer { state ->
            if (state == CastState.CONNECTED) {
                getConnectedCastDevice()?.deviceId?.let {
                    scanDeviceAdapter?.deviceConnected(
                        true,
                        it
                    )
                }
            } else if (state == CastState.NOT_CONNECTED) {
                getConnectedCastDevice()?.deviceId?.let {
                    scanDeviceAdapter?.deviceConnected(
                        false,
                        it
                    )
                }
            }

        })
    }

    private fun hideProgress(){
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            activity?.runOnUiThread {
                binding?.progressBar?.visibility = View.GONE
            }
        }, 1000)
    }

    private fun showProgress(){
        if (binding?.progressBar?.visibility == View.GONE)
            binding?.progressBar?.visibility = View.VISIBLE
    }

    private fun getConnectionStatus() {
        val isConnected = isCastingConnected()
        if (isConnected != null) {
            getConnectedCastDevice()?.deviceId?.let {
                scanDeviceAdapter?.deviceConnected(
                    isConnected,
                    it
                )
            }
        } else {
            getMediaRouter()?.let { router ->
                CastHelper.getCastEnabled(router, ::castingEnabledCallback)
            }
        }
    }

    private fun castingEnabledCallback(isConnected: Boolean, device: CastDevice?) {
        setConnectionInfo(isConnected, device)

        device?.deviceId?.let { scanDeviceAdapter?.deviceConnected(isConnected, it) }
    }

    private fun checkWifiNetwork() {
        Log.d(
            "ScanDeviceFragment",
            "onViewCreated A13 : <<<<11" + Utils.isNetworkConnected(context)
        )
        if (Utils.isNetworkConnected(context)) {
            fetchDeviceList()
        } else {
            showProgress()
            binding?.llNoNetwork?.visibility = View.VISIBLE
            binding?.llNoDeviceFound?.visibility = View.GONE
            binding?.llItemLayout?.visibility = View.GONE
            binding?.llScanning?.visibility = View.GONE
            hideProgress()
        }
    }

    private fun fetchDeviceList() {
//        showLoader()
        showProgress()
        binding?.llScanning?.visibility = View.VISIBLE
        binding?.llNoNetwork?.visibility = View.GONE
        binding?.llNoDeviceFound?.visibility = View.GONE
        binding?.llItemLayout?.visibility = View.GONE

        getMediaRouter()?.let { scanViewModel.fetchDeviceList(it) }
    }

    private fun itemClick(isConnect: Boolean, castModel: CastModel) {
        scanViewModel.showConnectionPrompt(context, ::actionPerform, isConnect, castModel)
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
        val result = Bundle().apply {
            putBoolean(
                AppConstants.START_SLIDESHOW,
                (fromSlideShow && isCastingConnected() == true)
            )
        }
        setFragmentResult(AppConstants.START_SLIDESHOW_REQUEST_KEY, result)
        findNavController().navigateUp()
    }
}