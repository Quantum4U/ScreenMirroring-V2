package com.example.projectorcasting.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.mediarouter.media.MediaRouter
import com.example.projectorcasting.casting.model.CastModel
import com.example.projectorcasting.casting.utils.CastHelper
import com.example.projectorcasting.models.MediaData
import com.example.projectorcasting.utils.AppUtils
import javax.inject.Inject
import kotlin.reflect.KFunction2

class VideoViewModel @Inject constructor() : ViewModel() {

    val videosList by lazy {
        MutableLiveData<ArrayList<MediaData>>()
    }

    fun fetchVideoList(context: Context) {
        videosList.postValue(AppUtils.getAllGalleryVideos(context))
    }


}