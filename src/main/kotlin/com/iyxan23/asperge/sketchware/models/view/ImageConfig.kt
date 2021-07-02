package com.iyxan23.asperge.sketchware.models.view

import kotlinx.serialization.Serializable

@Serializable
data class ImageConfig(
    val resName: String? = null,
    val rotate: Int,
    val scaleType: String,
)
