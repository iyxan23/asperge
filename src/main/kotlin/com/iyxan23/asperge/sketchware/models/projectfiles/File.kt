package com.iyxan23.asperge.sketchware.models.projectfiles

import com.iyxan23.asperge.sketchware.models.projectfiles.file.FileItem
import kotlinx.serialization.Serializable

@Serializable
data class File(
    val activities: List<FileItem>,
    val customViews: List<FileItem>,
)
