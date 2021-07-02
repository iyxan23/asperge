package com.iyxan23.asperge.swproject.logic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Represents a block in logic
@Serializable
data class Block(
    val color: Int,
    val id: String,
    val nextBlock: Int,
    val opCode: String,
    val parameters: List<String>,

    val spec: String,

    val subStack1: Int,
    val subStack2: Int,

    @SerialName("type")
    val returnType: String,

    val typeName: String,
)