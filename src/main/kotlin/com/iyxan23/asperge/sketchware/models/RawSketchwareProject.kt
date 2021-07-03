package com.iyxan23.asperge.sketchware.models

import com.iyxan23.asperge.sketchware.parsers.*

data class RawSketchwareProject(
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
}