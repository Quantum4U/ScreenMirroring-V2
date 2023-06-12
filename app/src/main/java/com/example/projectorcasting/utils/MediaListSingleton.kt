package com.example.projectorcasting.utils

import com.example.projectorcasting.models.FolderModel
import com.example.projectorcasting.models.MediaData
import com.example.projectorcasting.models.SectionModel

object MediaListSingleton {

    private var galleryImageFolderList: ArrayList<FolderModel>? = null
    private var galleryImageSectionedList: ArrayList<MediaData>? = arrayListOf()
    private var galleryVideoSectionedList: ArrayList<SectionModel>? = null
    private var galleryVideoFileList: List<MediaData>? = null
    private var galleryAudioFileList: List<MediaData>? = null

    fun setGalleryImageFolderList(list: ArrayList<FolderModel>?) {
        this.galleryImageFolderList = list?.let { ArrayList(it) }
    }

    fun getGalleryImageFolderList(): ArrayList<FolderModel>? {
        return galleryImageFolderList
    }
    fun setGalleryImageList(list: ArrayList<MediaData>?) {
        this.galleryImageSectionedList?.addAll(list!!)
    }

    fun getGalleryImageList(): ArrayList<MediaData>? {
        return galleryImageSectionedList
    }

    fun setGalleryVideoSectionedList(list: ArrayList<SectionModel>?) {
        this.galleryVideoSectionedList = list?.let { ArrayList(it) }
    }

    fun getGalleryVideoSectionedList(): ArrayList<SectionModel>? {
        return galleryVideoSectionedList
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