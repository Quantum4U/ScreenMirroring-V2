package com.example.projectorcasting.models

import android.graphics.Bitmap
import java.io.File

data class MediaData(
    val file: File? = null,
    val date: String? = null,
    val duration: String? = null,
    val bitmap: Bitmap? = null,
    val folderId: String? = null,
    val folderName: String? = null,
    val path: String? = null,
    var isCheck:Boolean?=null
)
