package com.iyxan23.asperge.sketchware.models

import com.iyxan23.asperge.sketchware.parsers.*
import java.io.File

data class DecryptedRawSketchwareProject(
    val logic: String,
    val view: String,
    val file: String,
    val library: String,
    val resource: String,
    val project: String,
) {
    fun parse(): SketchwareProject =
        SketchwareProject(
            LogicParser     (logic      ).parse(),
            ViewParser      (view       ).parse(),
            FileParser      (file       ).parse(),
            LibraryParser   (library    ).parse(),
            ResourceParser  (resource   ).parse(),
            ProjectParser   (project    ).parse(),
        )

    fun writeToFolder(folder: File) {
        if (!folder.exists()) folder.mkdir()

        File(folder, "logic"    ).writeText(logic   )
        File(folder, "view"     ).writeText(view    )
        File(folder, "file"     ).writeText(file    )
        File(folder, "library"  ).writeText(library )
        File(folder, "resource" ).writeText(resource)
        File(folder, "project"  ).writeText(project )
    }
}