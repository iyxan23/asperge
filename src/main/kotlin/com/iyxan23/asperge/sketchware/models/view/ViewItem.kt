package com.iyxan23.asperge.sketchware.models.view

import kotlinx.serialization.Serializable

@Serializable
data class ViewItem(
    val adSize: String,
    val adUnitId: String,
    val alpha: Float,
    val checked: Int,
    val choiceMode: Int,
    val clickable: Int,
    val customView: String,
    val dividerHeight: Int,
    val enabled: Int,
    val firstDayOfWeek: Int,
    val id: String,
    val image: ImageConfig,
    val indeterminate: String,
    val index: Int,
    val layout: LayoutConfig,
    val max: Int,
    val parent: String? = null, // For some reasons, fab doesn't have parent and preId
    val parentType: Int,
    val preParent: String? = null,
    val preId: String? = null,
    val preIndex: Int,
    val preParentType: Int,
    val progress: Int,
    val progressStyle: String,
    val scaleX: Float,
    val scaleY: Float,
    val spinnerMode: Int,
    val text: TextConfig,
    val translationX: Float,
    val translationY: Float,
    val type: Int
)
