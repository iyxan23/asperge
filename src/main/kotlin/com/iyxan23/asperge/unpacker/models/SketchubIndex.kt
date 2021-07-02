package com.iyxan23.asperge.unpacker.models

import kotlinx.serialization.Serializable

@Serializable
data class SketchubIndex(
    val uid: String,
    val data: String,
    val version: String,
)
