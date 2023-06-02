package com.example.projectorcasting.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectorcasting.R
import com.example.projectorcasting.adapter.ScanDeviceAdapter
import com.example.projectorcasting.casting.model.CastModel
import com.example.projectorcasting.casting.utils.CastHelper
import com.example.projectorcasting.databinding.FragmentScandeviceBinding
import com.example.projectorcasting.viewmodels.ScanViewModel
import com.google.android.gms.cast.CastDevice
import com.google.android.gms.cast.framework.CastState

class ScanDeviceFragment : BaseFragment(R.layout.fragment_scandevice) {

    private val scanViewModel: ScanViewModel by viewModels()
    private var binding: FragmentScandeviceBinding? = null
    private var scanDeviceAdapter: ScanDeviceAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentScandeviceBinding.bind(view)
        observeCastingLiveData()
        observeDeviceList()

        binding?.ivBack?.setOnClickListener {
            exitPage()
        }

        binding?.ivRefresh?.setOnClickListener {
            checkWifiNetwork()
        }

        checkWifiNetwork()
        showNativeMedium(binding?.bottomBannerAd, activity)
    }

    private fun observeDeviceList() {
        scanViewModel.deviceList.observe(viewLifecycleOwner, Observer { list ->
            hideLoader()
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
//        if (Utils.isNetworkConnected(this)) {
        fetchDeviceList()
//        } else {
//            binding?.llNoNetwork?.visibility = View.VISIBLE
//            binding?.llNoDeviceFound?.visibility = View.GONE
//            binding?.llItemLayout?.visibility = View.GONE
//            binding?.llScanning?.visibility = View.GONE
//        }
    }

    private fun fetchDeviceList() {
        showLoader()
        binding?.llScanning?.visibility = View.VISIBLE
        binding?.llNoNetwork?.visibility = View.GONE
        binding?.llNoDeviceFound?.visibility = View.GONE
        binding?.llItemLayout?.visibility = View.GONE

        getMediaRouter()?.let { scanViewModel.fetchDeviceList(it) }
    }

    private fun itemClick(castModel: CastModel) {
        startCasting(castModel.routeInfo, castModel.castDevice)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun exitPage() {
        findNavController().navigateUp()
    }
}