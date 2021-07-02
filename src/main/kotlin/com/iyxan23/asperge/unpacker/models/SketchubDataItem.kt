package com.iyxan23.asperge.unpacker.models

import kotlinx.serialization.Serializable

@Serializable
data class SketchubDataItem(
    val name: String,
    val id: String,
    val type: String
)