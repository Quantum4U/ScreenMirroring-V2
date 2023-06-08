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
import javax.inject.Inject
import kotlin.reflect.KFunction2

class AudioViewModel @Inject constructor() : ViewModel() {

    val audiosList by lazy {
        MutableLiveData<ArrayList<MediaData>>()
    }


    fun fetchVideoList(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            audiosList.postValue(AppUtils.getAllGalleryAudios(context))
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


}