package com.iyxan23.asperge

import com.iyxan23.asperge.generator.java.NewJavaGenerator
import com.iyxan23.asperge.generator.manifest.AndroidManifestGenerator
import com.iyxan23.asperge.generator.xml.XmlLayoutGenerator
import com.iyxan23.asperge.sketchware.models.DecryptedRawSketchwareProject
import com.iyxan23.asperge.sketchware.models.RawSketchwareProject
import com.iyxan23.asperge.sketchware.models.SketchwareProject
import com.iyxan23.asperge.sketchware.models.projectfiles.logic.BaseLogicSection
import com.iyxan23.asperge.unpacker.Unpacker
import java.io.File
import java.nio.charset.StandardCharsets

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

            if (!options.printToStdout) {
                if (out.exists() && out.isFile) {
                    // Check if the user didn't specify the -f flag
                    if (!options.force) {
                        print("File $out already exists, override (use -f to override without questioning) [Y/N]? ")

                        val answer = readLine()

                        if (answer != null) {
                            if (answer.toLowerCase() != "y") return
                            else {
                                println("Got an invalid answer $answer, exiting")
                                return
                            }

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
            } else {
                println(Decryptor.decrypt(File(options.filePath).readBytes()))
            }
        }

        is CLIParser.GenerateOptions -> {
            val path = File(options.path)

            ////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // Arguments checking
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////

            // Used to check if we need to check if an activity / layout should be generated
            val restrictActivities = options.activities.isNotEmpty()
            val restrictLayouts = options.layouts.isNotEmpty()

            // Some checks to make sure that the arguments given are valid
            if (
                (options.layoutOnly && options.javaOnly) ||
                (options.manifestOnly && options.layoutOnly) ||
                (options.manifestOnly && options.javaOnly)
            ) {
                println("You can only choose one flag between --layout-only, --java-only and --manifest-only")
                return
            }

            if (options.javaOnly && restrictLayouts) {
                println("Cannot restrict layouts when we can only generate java codes")
                return
            }

            if (options.layoutOnly && restrictActivities) {
                println("Cannot restrict activities when we can only generate xml layout codes")
                return
            }

            if (options.printCodeToStdout && options.out.isNotBlank()) {
                println("Cannot print to stdout and output a file at the same time")
                return
            }

            val out: File =
                if (options.out.isBlank())
                    File(if (path.isFile) path.nameWithoutExtension else path.name + "_gen")

                else File(options.out)

            // check if out already exists if we don't print to stdout
            if (!options.printCodeToStdout) {
                if (out.exists()) {
                    println("$out already exists as a ${if (out.isFile) "file" else "folder"}")
                    return
                }

                // mkdir the output folder if the user doesn't want to print to stdout
                out.mkdir()
            }

            if (!path.exists()) {
                println("$path doesn't exist, make sure you spelled it correctly")
                return
            }

            ////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // Reading project
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////

            val sketchwareProject: SketchwareProject

            // Now figure out how to transform the given path into a sketchware project
            if (!path.isFile) {
                // This is a folder, it might be a decrypted sketchware project, or an encrypted sketchware project
                // But first, we should check the project structure
                val requiredFiles = arrayOf("logic", "view", "file", "library", "resource", "project")

                requiredFiles.forEach {
                    if (!File(path, it).exists()) {
                        println("The file $it doesn't exist inside the folder, make sure to select the right folder")
                        return
                    }
                }

                // And then check if one of the project file is encrypted or not (follows utf-8 or not)
                if (isUTF8(File(path, "logic").readBytes())) {
                    // This is a decrypted project! read the files then
                    val logic = File(path, "logic").readText()
                    val view = File(path, "view").readText()
                    val file = File(path, "file").readText()
                    val library = File(path, "library").readText()
                    val resource = File(path, "resource").readText()
                    val project = File(path, "project").readText()

                    sketchwareProject =
                        DecryptedRawSketchwareProject(logic, view, file, library, resource, project).parse()
                } else {
                    // This is encrypted, read the files, and decrypt it
                    val logic = File(path, "logic").readBytes()
                    val view = File(path, "view").readBytes()
                    val file = File(path, "file").readBytes()
                    val library = File(path, "library").readBytes()
                    val resource = File(path, "resource").readBytes()
                    val project = File(path, "project").readBytes()

                    sketchwareProject =
                        RawSketchwareProject(logic, view, file, library, resource, project).decrypt().parse()
                }
            } else {
                // This should be a sketchware backup file, unpack it and parse it
                val unpacked = Unpacker.unpack(path.absolutePath)
                sketchwareProject = unpacked.decrypt().parse()
            }

            // Initialize some folders
            val layoutFolder = File(out, "res/layout")
            val codeFolder =
                File(out, "java/${sketchwareProject.project.packageName.replace(".", "/")}/")
            val manifestFile = File(out, "AndroidManifest.xml")

            // Create the folders if we don't print to stdout
            if (!options.printCodeToStdout) {
                if (!options.javaOnly   && !options.manifestOnly) layoutFolder.mkdirs()
                if (!options.layoutOnly && !options.manifestOnly) codeFolder.mkdirs()
                if (!options.javaOnly   && !options.layoutOnly  ) manifestFile.createNewFile()
            }

            ////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // Start generating
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////

            if (!options.javaOnly && !options.layoutOnly) {
                val manifest = AndroidManifestGenerator(
                    sketchwareProject.file,
                    sketchwareProject.project
                ).generate()

                if (options.printCodeToStdout) {
                    println(manifest)
                } else {
                    manifestFile.writeText(manifest)
                }
            }

            if (options.manifestOnly) return

            // Now sort these sections into a map of activities, because every sections are mixed together
            val activities = HashMap<String, ArrayList<BaseLogicSection>>()

            sketchwareProject.logic.sections.forEach { section ->
                if (!activities.containsKey(section.name)) {
                    activities[section.name] = ArrayList()
                }

                activities[section.name]!!.add(section)
            }

            // This is used for the java generator to generate declaration, and findViewByIds
            val viewIdTypes = HashMap<String, Pair<List<String>, List<String>>>()

            // At the same time, generate xml layouts
            sketchwareProject.view.sections.forEach { section ->
                // Check if the extension is not .xml_fab
                // TODO: 7/7/21 understand what xml_fab is
                if (section.ext == "xml_fab") return@forEach

                val generator = XmlLayoutGenerator(
                    section,
                    sketchwareProject.resource,
                    sketchwareProject.file,
                    sketchwareProject.project
                )

                // Used for java generator to declare variable view ids n stuff
                viewIdTypes[section.name] = Pair(generator.getViewIDs(), generator.getViewTypes())

                // Make sure to check if the user wants the layout
                if (options.javaOnly) return@forEach

                // Check if they restrict layouts
                if (restrictLayouts) {
                    // Also check if they wanted to include this layout file
                    if (!options.layouts.contains(section.name)) return@forEach // No, then just continue the loop
                }

                val code = generator.generate()

                // Check if the user wants the code to be saved to a file instead of printing it to the terminal
                if (!options.printCodeToStdout) {
                    // Write code
                    File(layoutFolder, "${section.name}.xml").writeText(code)
                } else {
                    println(code)
                }
            }

            // Make sure that the user wants the java code
            if (options.layoutOnly) return

            // Generate java codes
            viewIdTypes.keys.forEach { layoutName ->
                val activityName = "${layoutName.capitalize()}Activity"

                // Check if they restrict activities
                if (restrictActivities) {
                    // Check if this activity is included
                    if (!options.activities.contains(activityName)) return@forEach // then just continue
                }

                // Generate code
                val code =
                    NewJavaGenerator(
                        activities[activityName] as List<BaseLogicSection>,
                        viewIdTypes[layoutName]!!.first,
                        viewIdTypes[layoutName]!!.second,
                        activityName,
                        layoutName,
                        sketchwareProject.project
                    ).generate()

                // Check if the user wants the code to be saved to a file instead of printing it to the terminal
                if (!options.printCodeToStdout) {
                    // Write code
                    File(codeFolder, "$activityName.java").writeText(code)
                } else {
                    println(code)
                }
            }
        }
    }
}

// Used to check if a file is encrypted or not
private fun isUTF8(inputBytes: ByteArray): Boolean {
    val converted = String(inputBytes, StandardCharsets.UTF_8)
    val outputBytes = converted.toByteArray(StandardCharsets.UTF_8)

    return inputBytes.contentEquals(outputBytes)
}
