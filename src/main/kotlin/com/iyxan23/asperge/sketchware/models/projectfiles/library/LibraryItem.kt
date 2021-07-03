package com.iyxan23.asperge.sketchware.models.projectfiles.library

import kotlinx.serialization.Serializable

@Serializable
data class LibraryItem(
    val adUnits: List<String>,
    val data: String,
    val libType: Int,
    val reserved1: String,
    val reserved2: String,
    val reserved3: String,
    val testDevices: List<String>,
    val useYn: String,
)