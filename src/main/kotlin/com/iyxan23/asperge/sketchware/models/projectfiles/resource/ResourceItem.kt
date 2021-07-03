package com.iyxan23.asperge.sketchware.models.projectfiles.resource

import kotlinx.serialization.Serializable

@Serializable
data class ResourceItem(
    val resFullName: String,
    val resName: String,
    val resType: Int,
)