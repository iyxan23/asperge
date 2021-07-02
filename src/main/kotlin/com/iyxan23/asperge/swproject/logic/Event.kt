package com.iyxan23.asperge.swproject.logic

import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val eventName: String,
    val eventType: Int,
    val targetId: String,
    val targetType: Int,
)