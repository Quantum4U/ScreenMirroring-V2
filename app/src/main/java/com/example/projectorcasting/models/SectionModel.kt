package com.example.projectorcasting.models

data class SectionModel(
    val date: String? = null,
    val sectionList: List<MediaData>? = null,
    var isCheck: Boolean? = null,
    var totalSelected: Int? = null
)
