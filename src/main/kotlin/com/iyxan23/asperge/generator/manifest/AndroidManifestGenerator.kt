package com.iyxan23.asperge.generator.manifest

import com.iyxan23.asperge.sketchware.models.projectfiles.File
import com.iyxan23.asperge.sketchware.models.projectfiles.Project
import org.redundent.kotlin.xml.xml

class AndroidManifestGenerator(
    val file: File,
    val project: Project
) {
    fun generate(): String {
        return xml("manifest") {
            attribute("xmlns:android", "http://schemas.android.com/apk/res/android")
            attribute("package", project.packageName)

            xml("application") {
                attribute("android:label", project.appName)

                file.activities.forEach { activity ->
                    xml("activity") {
                        attribute("android:name", ".${activity.fileName.toLowerCase().capitalize()}Activity")

                        // Ad intent the launcher intent filter if the current activity is MainActivity
                        if (activity.fileName == "main") {
                            xml("intent-filter") {
                                xml("action") {
                                    attribute("android:name", "android.intent.action.MAIN")
                                }

                                xml("category") {
                                    attribute("android:name", "android.intent.category.LAUNCHER")
                                }
                            }
                        }
                    }
                }
            }

        }.toString(true)
    }
}