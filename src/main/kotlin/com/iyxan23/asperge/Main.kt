package com.iyxan23.asperge

import com.iyxan23.asperge.sketchware.parsers.*
import com.iyxan23.asperge.unpacker.Unpacker

fun main(args: Array<String>) {
    val unpacked = Unpacker.unpack(args[0])
    println(unpacked)
    println(LogicParser(unpacked.logic).parse())
    println(ViewParser(unpacked.view).parse())
    println(FileParser(unpacked.file).parse())
    println(LibraryParser(unpacked.library).parse())
    println(ResourceParser(unpacked.resource).parse())
    println(ProjectParser(unpacked.project).parse())
}