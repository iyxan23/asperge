package com.iyxan23.asperge.swproject.logic

import kotlinx.serialization.Serializable

@Serializable
data class Component(
    val componentId: String,
    val param1: String,
    val param2: String,
    val param3: String,
    val type: String,
)