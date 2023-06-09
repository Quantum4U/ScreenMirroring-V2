package com.example.projectorcasting.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectorcasting.casting.model.CastModel
import com.example.projectorcasting.models.MediaData
import com.example.projectorcasting.utils.AppUtils
import com.example.projectorcasting.utils.PromptHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.reflect.KFunction2

class DashboardViewModel : ViewModel() {

    val imagesList by lazy {
        MutableLiveData<ArrayList<MediaData>>()
    }

    fun showConnectionPrompt(
        context: Context?,
        actionPerform: KFunction2<Boolean, CastModel?, Unit>,
        isConnect: Boolean,
        castModel: CastModel?
    ) {
        PromptHelper.showConnectionPrompt(context, actionPerform, isConnect, castModel)
    }

    fun fetchImages(context: Context?){
        viewModelScope.launch(Dispatchers.Default) {
            imagesList.postValue(context?.let { AppUtils.getAllGalleryImages(it) })
        }
    }
}