package com.example.projectorcasting.utils

import com.example.projectorcasting.models.MediaData

object MediaListSingleton {

    private var galleryVideoFileList: HashMap<Int, List<MediaData>>? = null


    fun setGalleryVideoList(list: HashMap<Int, List<MediaData>>?) {
        this.galleryVideoFileList = list?.let { HashMap(it) }
    }

    fun getGalleryVideoList(): HashMap<Int, List<MediaData>>? {
        return galleryVideoFileList
    }

}