package com.iyxan23.asperge.sketchware.models

import com.iyxan23.asperge.sketchware.models.projectfiles.*

data class SketchwareProject(
    val logic: Logic,
    val view: View,
    val file: File,
    val library: Library,
    val resource: Resource,
    val project: Project,
)
