package com.example.projectorcasting.utils

import com.example.projectorcasting.models.MediaData

object MediaListSingleton {

    private var galleryVideoFileList: HashMap<String, List<MediaData>>? = null


    fun setGalleryVideoList(list: HashMap<String, List<MediaData>>?) {
        this.galleryVideoFileList = list?.let { HashMap(it) }
    }

    fun getGalleryVideoList(): HashMap<String, List<MediaData>>? {
        return galleryVideoFileList
    }

}