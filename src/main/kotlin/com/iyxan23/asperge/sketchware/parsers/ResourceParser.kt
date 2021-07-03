package com.iyxan23.asperge.sketchware.parsers

import com.iyxan23.asperge.sketchware.models.projectfiles.Resource
import com.iyxan23.asperge.sketchware.models.projectfiles.resource.ResourceItem
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ResourceParser(content: String) : Parser<Resource>(content) {

    override fun parse(): Resource {
        val resources = HashMap<String, List<ResourceItem>>()

        val requiredResources = arrayOf("images", "sounds", "fonts")

        while (currentLine != null) {
            if (currentLine!!.startsWith("@")) {
                val resSectionName = currentLine!!.substring(1, currentLine!!.length)

                advance()

                resources[resSectionName] = parseResources()
            } else {
                advance()
            }
        }

        // Do a check
        requiredResources.forEach {
            if (!resources.containsKey(it)) throw RuntimeException("Required resource section with name $it isn't present in the resource file")
        }

        return Resource(
            resources["images"  ]!!,
            resources["sounds"  ]!!,
            resources["fonts"   ]!!,
        )
    }

    private fun parseResources(): List<ResourceItem> {
        val result = ArrayList<ResourceItem>()

        while (!currentLine!!.trim().startsWith("@") && currentLine!!.trim().isNotEmpty()) {
            result.add(Json.decodeFromString(currentLine!!))

            advance()
        }

        return result
    }
}