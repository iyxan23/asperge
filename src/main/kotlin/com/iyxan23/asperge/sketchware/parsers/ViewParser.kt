package com.iyxan23.asperge.sketchware.parsers

import com.iyxan23.asperge.sketchware.models.projectfiles.View
import com.iyxan23.asperge.sketchware.models.projectfiles.view.ViewItem
import com.iyxan23.asperge.sketchware.models.projectfiles.view.ViewSection
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ViewParser(content: String) : Parser<View>(content) {

    override fun parse(): View {
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
            try {
                result.add(Json.decodeFromString(currentLine!!))

                advance()

            } catch (e: Exception) {
                if (e.message!!.contains("Encountered an unknown key 'convert'."))
                    throw RuntimeException("Modded sketchware projects are not supported yet")

                else throw e
            }
        }

        return result
    }
}