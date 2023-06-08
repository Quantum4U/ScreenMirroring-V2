package com.example.projectorcasting.utils

import com.example.projectorcasting.models.MediaData

object MediaListSingleton {

    private var galleryVideoFileHashMap: HashMap<String, List<MediaData>>? = null
    private var galleryVideoFileList: List<MediaData>? = null
    private var galleryAudioFileList: List<MediaData>? = null


    fun setGalleryVideoHashMap(list: HashMap<String, List<MediaData>>?) {
        this.galleryVideoFileHashMap = list?.let { HashMap(it) }
    }

    fun getGalleryVideoHashMap(): HashMap<String, List<MediaData>>? {
        return galleryVideoFileHashMap
    }

    fun setGalleryVideoList(list: List<MediaData>?) {
        this.galleryVideoFileList = list?.let { ArrayList(it) }
    }

    fun getGalleryVideoList(): List<MediaData>? {
        return galleryVideoFileList
    }

    fun setGalleryAudioList(list: List<MediaData>?) {
        this.galleryAudioFileList = list?.let { ArrayList(it) }
    }

    fun getGalleryAudioList(): List<MediaData>? {
        return galleryAudioFileList
    }

}