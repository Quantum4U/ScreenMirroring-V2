package com.example.projectorcasting.utils

import com.example.projectorcasting.models.MediaData

object MediaListSingleton {

    private var galleryImageFileHashMap: LinkedHashMap<String, List<MediaData>>? = null
    private var galleryVideoFileHashMap: LinkedHashMap<String, List<MediaData>>? = null
    private var galleryVideoFileList: List<MediaData>? = null
    private var galleryAudioFileList: List<MediaData>? = null

    fun setGalleryImageHashMap(list: LinkedHashMap<String, List<MediaData>>?) {
        this.galleryImageFileHashMap = list?.let { LinkedHashMap(it) }
    }

    fun getGalleryImageHashMap(): LinkedHashMap<String, List<MediaData>>? {
        return galleryImageFileHashMap
    }

    fun setGalleryVideoHashMap(list: LinkedHashMap<String, List<MediaData>>?) {
        this.galleryVideoFileHashMap = list?.let { LinkedHashMap(it) }
    }

    fun getGalleryVideoHashMap(): LinkedHashMap<String, List<MediaData>>? {
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