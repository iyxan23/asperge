package com.iyxan23.asperge.swproject.view

import kotlinx.serialization.Serializable

@Serializable
data class ImageConfig(
    val rotate: Int,
    val scaleType: String,
)
