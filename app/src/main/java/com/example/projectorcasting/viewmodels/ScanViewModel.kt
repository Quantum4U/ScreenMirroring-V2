package com.example.projectorcasting.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.mediarouter.media.MediaRouter
import com.example.projectorcasting.casting.model.CastModel
import com.example.projectorcasting.casting.utils.CastHelper
import com.example.projectorcasting.utils.PromptHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.reflect.KFunction2

@HiltViewModel
class ScanViewModel @Inject constructor() : ViewModel() {

    val deviceList by lazy {
        MutableLiveData<ArrayList<CastModel>>()
    }

    fun fetchDeviceList(mediaRouter: MediaRouter) {
        deviceList.postValue(CastHelper.getAvailableDevices(mediaRouter))
    }

    fun showConnectionPrompt(context: Context?,actionPerform: KFunction2<Boolean, CastModel?, Unit>, isConnect: Boolean, castModel: CastModel?){
        PromptHelper.showConnectionPrompt(
            context,
            actionPerform,
            isConnect,
            castModel,
            ""
        )
    }
}