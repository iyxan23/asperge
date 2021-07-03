package com.iyxan23.asperge.sketchware.models.projectfiles.file

import kotlinx.serialization.Serializable

// Can be either activity or CustomView, for some reason
@Serializable
data class FileItem(
    val fileName: String,
    val fileType: Int,
    val keyboardSetting: Int,
    val options: Int,
    val orientation: Int,
    val theme: Int,
)
