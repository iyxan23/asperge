package com.iyxan23.asperge.unpacker

import com.iyxan23.asperge.Decryptor
import com.iyxan23.asperge.sketchware.RawSketchwareProject
import com.iyxan23.asperge.unpacker.models.SketchubDataItem
import com.iyxan23.asperge.unpacker.models.SketchubIndex
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
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

    fun unpack(path: String): RawSketchwareProject {
        when (getProjectType(path)) {
            ProjectType.SKETCHUB -> return unpackSketchub(path)

            null ->
                throw RuntimeException(
                    "The specified project file has an unknown file format, supported type(s): sketchub (.sh)"
                )
        }
    }

    private fun unpackSketchub(path: String): RawSketchwareProject {
        val zip = ZipFile(path)

        // Get index.json
        val indexRaw = String(
            zip.getInputStream(
                zip.getEntry("temp//index.json")
            ).readBytes()
        )

        val index = Json.decodeFromString<SketchubIndex>(indexRaw)

        // And read the data
        val data = Json.decodeFromString<List<SketchubDataItem>>(index.data)

        // Get the project files
        val projectFiles = HashMap<String, String>()
        val projectFilenames = arrayOf("logic", "view", "file", "library", "resource", "project")

        data.forEach { item ->
            if (projectFilenames.contains(item.name))
                projectFiles[item.name] =
                    Decryptor.decrypt(
                        zip.getInputStream(
                            zip.getEntry("temp//${item.id}")
                        ).readBytes()
                    )
        }

        // Check to make sure that these project files are initialized
        projectFilenames.forEach { filename ->
            if (!projectFiles.containsKey(filename))
                throw RuntimeException("$filename doesn't exist inside the sketchub project")
        }

        // And finally just return the sketchware project
        return RawSketchwareProject(
            projectFiles["logic"    ]!!,
            projectFiles["view"     ]!!,
            projectFiles["file"     ]!!,
            projectFiles["library"  ]!!,
            projectFiles["resource" ]!!,
            projectFiles["project"  ]!!,
        )
    }
}