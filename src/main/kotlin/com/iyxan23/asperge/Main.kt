package com.iyxan23.asperge

import com.iyxan23.asperge.unpacker.Unpacker
import java.io.File

fun main(args: Array<String>) {
    when (val options = CLIParser.process(args.toList())) {
        is CLIParser.ExtractOptions -> {
            val unpacked = Unpacker.unpack(options.filePath)

            if (options.dontDecrypt) {
                unpacked.writeToFolder(File(options.out))
            } else {
                unpacked.decrypt().writeToFolder(File(options.out))
            }
        }

        is CLIParser.DecryptOptions -> {
            val out = File(options.out)

            if (out.exists() && out.isFile) {
                // Check if the user didn't specify the -f flag
                if (!options.force) {
                    print("File $out already exists, override (use -f to override without questioning) [Y/N]? ")

                    val answer = readLine()

                    if (answer != null) {
                        if (answer.toLowerCase() != "y") return
                        else println("Got an invalid answer $answer, exiting"); return

                    } else {
                        println("No input, exiting")
                        return
                    }
                } else {
                    out.writeText("")
                }

            } else {
                out.createNewFile()
            }

            out.writeText(
                Decryptor.decrypt(
                    File(options.filePath).readBytes()
                )
            )
        }
    }
}