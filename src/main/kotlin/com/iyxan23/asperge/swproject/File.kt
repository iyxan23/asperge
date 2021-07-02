package com.iyxan23.asperge.swproject

import com.iyxan23.asperge.swproject.file.FileItem

data class File(
    val activities: List<FileItem>,
    val customViews: List<FileItem>,
)
