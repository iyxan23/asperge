package com.iyxan23.asperge.sketchware.models.view

import kotlinx.serialization.Serializable

@Serializable
data class LayoutConfig(
    val backgroundColor: Int,
    val gravity: Int,
    val height: Int,
    val layoutGravity: Int,
    val marginBottom: Int,
    val marginLeft: Int,
    val marginRight: Int,
    val marginTop: Int,
    val orientation: Int,
    val paddingBottom: Int,
    val paddingLeft: Int,
    val paddingRight: Int,
    val paddingTop: Int,
    val weight: Int,
    val weightSum: Int,
    val width: Int
)