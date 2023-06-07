package com.example.projectorcasting.viewmodels

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectorcasting.casting.model.CastModel
import com.example.projectorcasting.models.MediaData
import com.example.projectorcasting.utils.AppUtils
import com.example.projectorcasting.utils.PromptHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import kotlin.reflect.KFunction2

class VideoViewModel @Inject constructor() : ViewModel() {

    val videosList by lazy {
        MutableLiveData<ArrayList<MediaData>>()
    }

    val thumbFile by lazy {
        MutableLiveData<File?>()
    }

    fun fetchVideoList(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            videosList.postValue(AppUtils.getAllGalleryVideos(context))
        }

    }

    fun showConnectionPrompt(
        context: Context?,
        actionPerform: KFunction2<Boolean, CastModel?, Unit>,
        isConnect: Boolean,
        castModel: CastModel?
    ) {
        PromptHelper.showConnectionPrompt(context, actionPerform, isConnect, castModel)
    }

    fun saveThumbnailAndPlayMedia(bitmap: Bitmap?){
        viewModelScope.launch(Dispatchers.IO) {
            thumbFile.postValue(AppUtils.saveTempThumb(bitmap))
        }
    }

    fun setThumbFile(){
        thumbFile.postValue(null)
    }

}