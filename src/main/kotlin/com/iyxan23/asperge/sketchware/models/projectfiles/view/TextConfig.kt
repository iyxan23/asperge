package com.iyxan23.asperge.sketchware.models.projectfiles.view

import kotlinx.serialization.Serializable

@Serializable
data class TextConfig(
    val hint: String,
    val hintColor: Int,
    val imeOption: Int,
    val inputType: Int,
    val line: Int,
    val singleLine: Int,
    val text: String,
    val textColor: Int,
    val textFont: String,
    val textSize: Int,
    val textType: Int,
)