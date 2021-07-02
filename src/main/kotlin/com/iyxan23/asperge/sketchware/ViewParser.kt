package com.iyxan23.asperge.sketchware

import com.iyxan23.asperge.sketchware.models.View
import com.iyxan23.asperge.sketchware.models.view.ViewItem
import com.iyxan23.asperge.sketchware.models.view.ViewSection
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ViewParser(content: String) {

    private var line = 0
    private var lines = content.lines()

    private var currentLine: String? = lines[line]

    private fun advance() {
        line++

        currentLine = if (line >= lines.size) null else lines[line]
    }

    fun parse(): View {
        val sections = ArrayList<ViewSection>()

        while (currentLine != null) {
            if (currentLine!!.startsWith("@"))
                sections.add(parseSection())

            advance()
        }

        return View(sections)
    }

    private fun parseSection(): ViewSection {
        val splitHeaders = currentLine!!.substring(1, currentLine!!.length).split(".")

        val name = splitHeaders[0]
        val ext = splitHeaders[1]

        advance()

        return ViewSection(name, ext, parseView())
    }

    // Used to parse serializable objects, such as Component, Event, etc
    private fun parseView(): List<ViewItem> {
        val result = ArrayList<ViewItem>()

        while (currentLine!!.trim().isNotEmpty()) {
            result.add(Json.decodeFromString(currentLine!!))

            advance()
        }

        return result
    }
}