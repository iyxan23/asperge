package com.iyxan23.asperge

import com.iyxan23.asperge.unpacker.Unpacker

object CLIParser {
    fun process(args: List<String>): Options? {
        if (args.isEmpty()) {
            defaultHelp()
            return null
        }

        when (args[0]) {
            "help" -> {
                if (args.size > 1) {
                    showHelp(args[1])
                } else {
                    defaultHelp()
                }

                return null
            }

            "extract" -> {
                val filename = args[1]
                var output = filename.split(".")[0]
                var dontDecrypt = false

                val arguments = ArgumentsParser(
                    listOf("--dont-decrypt"),
                    listOf("--out")
                ).parse(args.subList(2, args.size))

                arguments.forEach {
                    if (it.first == "--dont-decrypt") dontDecrypt = true
                    else if (it.first == "--out") output = it.second!!
                }

                return ExtractOptions(filename, output, dontDecrypt)
            }

            "decrypt" -> {
                TODO()
            }

            "generate", "gen" -> {
                TODO()
            }

            else -> {
                println("Unknown command ${args[0]}")
                return null
            }
        }
    }

    private fun defaultHelp() {
        println("""
            Asperge - Sketchware project extractor, and code generator
            
            Commands:
                - help: Shows this help page
                - extract: Extracts a sketchware project
                - decrypt: Decrypts a sketchware-encrypted file
                - generate: Generates java and xml layout code from a sketchware project
            
            Examples:
                - asperge help extract: Shows help about extract
                - asperge extract project.sh: Extracts project.sh
                - asperge decrypt file: Decrypts file
                - asperge generate project.sh --out myProject: Generates java and xml layout to myFolder
               
            Licensed under Apache 2.0 <https://www.apache.org/licenses/LICENSE-2.0>
            Project Source Code: https://github.com/Iyxan23/asperge
            Made by Iyxan23 with <3
        """.trimIndent())
    }

    private fun showHelp(name: String) {
        when (name) {
            "help" -> {
                println("help prints the help page you")
            }

            "extract" -> {
                println("""
                    Asperge - Sketchware project extractor, and code generator
                    
                    Supported filetype(s): ${
                        StringBuilder().apply {
                            Unpacker.typeExtensions.forEach {
                                append("${it.value.name.toLowerCase().capitalize()} (.${it.key}), ")
                            }
                        }
                    }
                    
                    Description:
                        `extract` Extracts a sketchware backup file into a folder
                    
                    Syntax:
                        extract (file) [--out path/to/folder/] [--dont-decrypt]
                    
                    Usage:
                        asperge extract project.sh: Extracts project.sh into folder ./output/
                        asperge extract project.sh --out folder/: Extracts project.sh into folder/
                        asperge extract project.sh --out folder/ --dont-decrypt: Extracts project.sh into folder/ without decrypting the files
                """.trimIndent())
            }

            "decrypt" -> {
                println("""
                    Asperge - Sketchware project extractor, and code generator
                    
                    Description:
                        `decrypt` Decrypts a sketchware-encrypted file
                    
                    Syntax:
                        decrypt (file) [--out path/to/file]
                    
                    Usage:
                        asperge decrypt file: Decrypts file and save it into file_decrypted
                        asperge decrypt file --out my_file: Decrypts file and save it into my_file
                """.trimIndent())
            }

            "generate" -> {
                println("""
                    Asperge - Sketchware project extractor, and code generator
                    
                    Description:
                        `generate` Generates java and xml layouts from a sketchware project
                    
                    Syntax:
                        (generate | gen) (project_backup_file | folder_of_sketchware_project) [--out path/to/folder] [--java-only] [--layout-only] [--activities (ExampleActivity,Example2Activity)] [--layouts (main,example)]
                    
                    Usage:
                        asperge gen my_project.sh: Generates java and xml layout files into ./my_project/
                        asperge gen extracted_project/: Generates java and xml layout files from an extracted project into ./extracted_project_gen/
                        asperge gen project.sh --out project_out/: Generates java and xml layout files to project/
                        asperge gen project.sh --java-only: Generates java files ONLY to project/
                        asperge gen project.sh --java-only --activities MainActivity: Generates MainActivity java file ONLY to project/
                        asperge gen project.sh --layout-only --layouts main: Generates main xml layout file ONLY to project/
                """.trimIndent())
            }

            else -> println("Unknown command $name")
        }
    }

    abstract class Options

    class ExtractOptions(
        val filePath: String,
        val out: String,
        val dontDecrypt: Boolean,
    ) : Options()
}