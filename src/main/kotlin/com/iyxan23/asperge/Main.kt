package com.iyxan23.asperge

import com.iyxan23.asperge.unpacker.Unpacker

fun main(args: Array<String>) {
    val unpacked = Unpacker.unpack(args[0])
    println(unpacked.parse())
}