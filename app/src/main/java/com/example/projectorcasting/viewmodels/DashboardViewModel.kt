package com.example.projectorcasting.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectorcasting.casting.model.CastModel
import com.example.projectorcasting.models.FolderModel
import com.example.projectorcasting.models.MediaData
import com.example.projectorcasting.models.SectionModel
import com.example.projectorcasting.utils.AppUtils
import com.example.projectorcasting.utils.PromptHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.reflect.KFunction2

class DashboardViewModel : ViewModel() {

    val imagesFolderList by lazy {
        MutableLiveData<ArrayList<FolderModel>>()
    }

    val imagesList by lazy {
        MutableLiveData<ArrayList<SectionModel>>()
    }

    val videosList by lazy {
        MutableLiveData<ArrayList<MediaData>>()
    }

    val audiosList by lazy {
        MutableLiveData<ArrayList<MediaData>>()
    }

    var isLoading = false

    fun showConnectionPrompt(
        context: Context?,
        actionPerform: KFunction2<Boolean, CastModel?, Unit>,
        isConnect: Boolean,
        castModel: CastModel?
    ) {
        PromptHelper.showConnectionPrompt(context, actionPerform, isConnect, castModel)
    }

    fun fetchImages(context: Context?){
        viewModelScope.launch(Dispatchers.IO) {
            isLoading = true
            imagesFolderList.postValue(context?.let { AppUtils.fetchImages(it) })

            withContext(Dispatchers.Main){
                isLoading = false
            }
        }
    }

    fun getAllGalleryImages(context: Context?){
        viewModelScope.launch(Dispatchers.IO) {
            imagesList.postValue(context?.let { AppUtils.getGalleryAllImages(it) })
        }
    }

    fun fetchVideoList(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading = true
            videosList.postValue(AppUtils.getAllGalleryVideos(context))

            withContext(Dispatchers.Main){
                isLoading = false
            }
        }

    }

    fun fetchAudioList(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading = true
            audiosList.postValue(AppUtils.getAllGalleryAudios(context))

            withContext(Dispatchers.Main){
                isLoading = false
            }
        }

    }



}