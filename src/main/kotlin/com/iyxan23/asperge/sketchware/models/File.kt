package com.iyxan23.asperge.sketchware.models

import com.iyxan23.asperge.sketchware.models.file.FileItem
import kotlinx.serialization.Serializable

@Serializable
data class File(
    val activities: List<FileItem>,
    val customViews: List<FileItem>,
)
