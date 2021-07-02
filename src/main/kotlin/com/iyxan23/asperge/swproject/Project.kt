package com.iyxan23.asperge.swproject

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Project(
    val customIcon: Boolean,

    @SerialName("app_name")
    val appName: String,

    @SerialName("my_sc_package_name")
    val packageName: String,

    @SerialName("my_ws_name")
    val workspaceName: String,

    @SerialName("sc_ver_name")
    val versionName: String,

    @SerialName("sc_ver_code")
    val versionCode: String,

    @SerialName("my_sc_reg_dt")
    val dateCrated: String,

    val colorPrimary: Int,
    val colorPrimaryDark: Int,
    val colorAccent: Int,
    val colorControlHighlight: Int,
    val colorControlNormal: Int,

    val sketchwareVer: Int,
)