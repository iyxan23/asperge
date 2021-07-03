package com.iyxan23.asperge.sketchware.parsers

import com.iyxan23.asperge.sketchware.models.projectfiles.File
import com.iyxan23.asperge.sketchware.models.projectfiles.file.FileItem
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class FileParser(content: String) : Parser<File>(content) {

    override fun parse(): File {
        val activities = ArrayList<FileItem>()
        val customViews = ArrayList<FileItem>()

        while (currentLine != null) {
            if (currentLine!!.trim() == "@activity")
                activities.addAll(parseFiles())

            if (currentLine!!.trim() == "@customview")
                customViews.addAll(parseFiles())

            advance()
        }

        return File(activities, customViews)
    }

    // Used to parse serializable objects, such as Component, Event, etc
    private fun parseFiles(): List<FileItem> {
        val result = ArrayList<FileItem>()

        advance()

        while (true) {
            try {
                result.add(Json.decodeFromString(currentLine!!))
                advance()

            // Can't catch the specific JsonDecodingException since it's an internal exception
            } catch (e: Exception) {
                break
            }
        }

        return result
    }
}