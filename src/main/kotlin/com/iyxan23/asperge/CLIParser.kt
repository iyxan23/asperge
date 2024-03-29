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
                val filename = args[1]
                var output = ""
                var force = false
                var printToStdout = false

                val arguments = ArgumentsParser(
                    listOf("-f", "--force", "--stdout", "-s"),
                    listOf("--out")
                ).parse(args.subList(2, args.size))

                arguments.forEach {
                    if (it.first == "--out") output = it.second!!
                    else if (it.first == "--stdout" || it.first == "-s") printToStdout = true
                    else if (it.first == "-f" || it.first == "--force") force = true
                }

                return DecryptOptions(filename, output, force, printToStdout)
            }

            "generate", "gen" -> {
                val path = args[1]
                var output = ""
                var printCodeToStdout = false

                var javaOnly = false
                var layoutOnly = false
                var manifestOnly = false

                var activities: List<String> = ArrayList()
                var layouts: List<String> = ArrayList()

                val arguments = ArgumentsParser(
                    listOf("--java-only", "--layout-only", "--manifest-only", "--stdout", "-s"),
                    listOf("--out", "--layouts", "--activities")
                ).parse(args.subList(2, args.size))

                arguments.forEach {
                    when (it.first) {
                        "--out" -> output = it.second!!

                        "--java-only" -> javaOnly = true
                        "--layout-only" -> layoutOnly = true
                        "--manifest-only" -> manifestOnly = true

                        "--activities" -> activities = it.second!!.split(",")
                        "--layouts" -> layouts = it.second!!.split(",")

                        "--stdout", "-s" -> printCodeToStdout = true
                    }
                }

                return GenerateOptions(path, output, javaOnly, layoutOnly, manifestOnly, activities, layouts, printCodeToStdout)
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
                asperge help extract
                    : Shows help about extract
                    
                asperge extract project.sh
                    : Extracts project.sh
                    
                asperge decrypt file
                    : Decrypts file
                    
                asperge generate project.sh --out myProject
                    : Generates java and xml layout to myProject
               
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
                        asperge extract project.sh
                            : Extracts project.sh into folder ./output/
                            
                        asperge extract project.sh --out folder/
                            : Extracts project.sh into folder/
                            
                        asperge extract project.sh --out folder/ --dont-decrypt
                            : Extracts project.sh into folder/ without decrypting the files
                """.trimIndent())
            }

            "decrypt" -> {
                println("""
                    Asperge - Sketchware project extractor, and code generator
                    
                    Description:
                        `decrypt` Decrypts a sketchware-encrypted file
                    
                    Syntax:
                        decrypt (file) [--out path/to/file] [-f | --force] [--stdout | -s]
                    
                    Usage:
                        asperge decrypt file
                            : Decrypts file and save it into file_decrypted
                            
                        asperge decrypt file --stdout
                            : Decrypts file and print it to stdout
                            
                        asperge decrypt file --out my_file
                            : Decrypts file and save it into my_file
                            
                        asperge decrypt file --out my_file -f
                            : Decrypts file and override my_file with the decrypted text if it exists
                """.trimIndent())
            }

            "generate" -> {
                println("""
                    Asperge - Sketchware project extractor, and code generator
                    
                    Description:
                        `generate` Generates java and xml layouts from a sketchware project
                    
                    Syntax:
                        (generate | gen) (project_backup_file | folder_of_sketchware_project) [--stdout | -s] [--out path/to/folder] [--java-only] [--layout-only] [--manifest-only] [--activities (ExampleActivity,Example2Activity)] [--layouts (main,example)]
                    
                    Usage:
                        asperge gen my_project.sh
                            : Generates java and xml layout files into ./my_project/
                            
                        asperge gen my_project.sh --stdout
                            : Generates java and xml layout files and echo it to stdout / output / terminal
                            
                        asperge gen extracted_project/
                            : Generates java and xml layout files from an extracted project into ./extracted_project_gen/
                            
                        asperge gen project.sh --out project_out/
                            : Generates java and xml layout files to project/
                            
                        asperge gen project.sh --java-only
                            : Generates java files ONLY to project/
                            
                        asperge gen project.sh --manifest-only
                            : Generates the AndroidManifest.xml file to project/
                            
                        asperge gen project.sh --java-only --activities MainActivity
                            : Generates MainActivity java file ONLY to project/
                            
                        asperge gen project.sh --layout-only --layouts main
                            : Generates main xml layout file ONLY to project/
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

    class DecryptOptions(
        val filePath: String,
        val out: String,
        val force: Boolean,
        val printToStdout: Boolean
    ) : Options()

    class GenerateOptions(
        val path: String,
        val out: String,
        val javaOnly: Boolean,
        val layoutOnly: Boolean,
        val manifestOnly: Boolean,
        val activities: List<String>,
        val layouts: List<String>,
        val printCodeToStdout: Boolean,
    ) : Options()
}