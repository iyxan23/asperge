package com.iyxan23.asperge.sketchware.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Project(
    @SerialName("sc_id")
    val id: String,

    @SerialName("custom_icon")
    val customIcon: Boolean,

    @SerialName("my_app_name")
    val appName: String,

    @SerialName("my_sc_pkg_name")
    val packageName: String,

    @SerialName("my_ws_name")
    val workspaceName: String,

    @SerialName("sc_ver_name")
    val versionName: String,

    @SerialName("sc_ver_code")
    val versionCode: String,

    @SerialName("my_sc_reg_dt")
    val dateCrated: String,

    // colors =============================

    @SerialName("color_primary")
    val colorPrimary: Double,

    @SerialName("color_primary_dark")
    val colorPrimaryDark: Double,

    @SerialName("color_accent")
    val colorAccent: Double,

    @SerialName("color_control_highlight")
    val colorControlHighlight: Double,

    @SerialName("color_control_normal")
    val colorControlNormal: Double,

    // colors =============================

    @SerialName("sketchware_ver")
    val sketchwareVer: Int,
)