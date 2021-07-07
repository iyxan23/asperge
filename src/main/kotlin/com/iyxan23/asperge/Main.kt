package com.iyxan23.asperge

import com.iyxan23.asperge.generator.java.NewJavaGenerator
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
        }

        is CLIParser.GenerateOptions -> {
            val path = File(options.path)
            val out = File(options.out)

            if (out.exists()) {
                println("$out already exists as a ${if (out.isFile) "file" else "folder"}")
                return
            }

            out.mkdir()

            if (!path.exists()) {
                println("$path doesn't exist, make sure you spelled it correctly")
                return
            }

            val sketchwareProject: SketchwareProject

            if (!path.isFile) {
                // This is a folder, it might be a decrypted sketchware project, or an encrypted sketchware project
                // But first, we should check the project structure
                val requiredFiles = arrayOf("logic", "view", "file", "library", "resources", "project")

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

            val layoutFolder = File(out, "res")
            val codeFolder =
                File(out, "java/${ sketchwareProject.project.packageName.replace(".", "/") }/")

            layoutFolder.mkdirs()
            codeFolder.mkdirs()

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
                val generator = XmlLayoutGenerator(
                    section,
                    sketchwareProject.resource,
                    sketchwareProject.file,
                    sketchwareProject.project
                )

                println(generator.generate())

                viewIdTypes[section.name] = Pair(generator.getViewIDs(), generator.getViewTypes())
            }

            // Generate java codes
            viewIdTypes.keys.forEach { layoutName ->
                val activityName = "${layoutName.capitalize()}Activity"
                println(
                    NewJavaGenerator(
                        activities[activityName] as List<BaseLogicSection>,
                        viewIdTypes[layoutName]!!.first,
                        viewIdTypes[layoutName]!!.second,
                        activityName,
                        layoutName,
                        sketchwareProject.project
                    ).generate()
                )
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