package com.iyxan23.asperge.sketchware.parsers

abstract class Parser<T>(content: String) {

    private var line = 0
    private var lines = content.lines()

    var currentLine: String? = lines[line]

    fun advance() {
        line++

        currentLine = if (line >= lines.size) null else lines[line]
    }

    abstract fun parse(): T
}