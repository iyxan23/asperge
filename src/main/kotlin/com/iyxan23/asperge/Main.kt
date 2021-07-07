package com.iyxan23.asperge

import com.iyxan23.asperge.unpacker.Unpacker
import java.io.File

fun main(args: Array<String>) {
    when (val options = CLIParser.process(args.toList())) {
        is CLIParser.ExtractOptions -> {
            val unpacked = Unpacker.unpack(args[0])

            if (options.dontDecrypt) {
                unpacked.writeToFolder(File(options.out))
            } else {
                unpacked.decrypt().writeToFolder(File(options.out))
            }
        }
    }
}