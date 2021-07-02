package com.iyxan23.asperge.sketchware

import com.iyxan23.asperge.sketchware.models.Library
import com.iyxan23.asperge.sketchware.models.library.LibraryItem
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class LibraryParser(content: String) : Parser<Library>(content) {

    override fun parse(): Library {
        val libraries = HashMap<String, LibraryItem>()

        val builtinLibraries = arrayOf("firebaseDB", "compat", "admob", "googleMap")

        while (currentLine != null) {
            if (currentLine!!.startsWith("@")) {
                val libName = currentLine!!.substring(1, currentLine!!.length)

                advance()

                libraries[libName] = Json.decodeFromString(currentLine!!)
            }

            advance()
        }

        // Do a check
        builtinLibraries.forEach {
            if (!libraries.containsKey(it)) throw RuntimeException("Required library with name $it isn't present in the library file")
        }

        return Library(
            libraries["firebaseDB"  ]!!,
            libraries["compat"      ]!!,
            libraries["admob"       ]!!,
            libraries["googleMap"   ]!!
        )
    }
}