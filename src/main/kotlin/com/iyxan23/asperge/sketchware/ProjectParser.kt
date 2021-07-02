package com.iyxan23.asperge.sketchware

import com.iyxan23.asperge.sketchware.models.Project
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ProjectParser(private val content: String) {
    fun parse(): Project = Json.decodeFromString(content)
}