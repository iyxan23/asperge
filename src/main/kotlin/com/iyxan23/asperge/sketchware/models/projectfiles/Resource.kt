package com.iyxan23.asperge.sketchware.models.projectfiles

import com.iyxan23.asperge.sketchware.models.projectfiles.resource.ResourceItem

data class Resource(
    val images: List<ResourceItem>,
    val sounds: List<ResourceItem>,
    val fonts: List<ResourceItem>,
)