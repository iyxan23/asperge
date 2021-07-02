package com.iyxan23.asperge

import jdk.nashorn.internal.parser.JSONParser
import java.io.File
import java.io.InputStream
import java.util.zip.ZipFile


object Unpacker {
    enum class ProjectType {
        SKETCHUB
    }

    // File extensions of the specified project types
    private val typeExtensions = mapOf(
        "sh" to ProjectType.SKETCHUB
    )

    private fun getProjectType(path: String): ProjectType? = typeExtensions[File(path).extension]

    fun unpack(path: String) {
        when (getProjectType(path)) {
            ProjectType.SKETCHUB -> unpackSketchub(path)

            null ->
                throw RuntimeException(
                    "The specified project file has an unknown file format, supported type(s): sketchub (.sh)"
                )
        }
    }

    private fun unpackSketchub(path: String) {
        TODO()
    }
}