package com.example.projectorcasting.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.mediarouter.media.MediaRouter
import com.example.projectorcasting.casting.model.CastModel
import com.example.projectorcasting.casting.utils.CastHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor() : ViewModel() {

    val deviceList by lazy {
        MutableLiveData<ArrayList<CastModel>>()
    }

    fun fetchDeviceList(mediaRouter: MediaRouter) {
        deviceList.postValue(CastHelper.getAvailableDevices(mediaRouter))
    }
}