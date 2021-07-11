package com.iyxan23.asperge.generator.manifest

import com.iyxan23.asperge.sketchware.models.projectfiles.File
import com.iyxan23.asperge.sketchware.models.projectfiles.Project
import org.redundent.kotlin.xml.xml

// TODO: 7/11/21 Add themes and such 
class AndroidManifestGenerator(
    val file: File,
    val project: Project
) {
    fun generate(): String {
        return xml("manifest") {
            attribute("xmlns:android", "http://schemas.android.com/apk/res/android")
            attribute("package", project.packageName)

            "application" {
                attribute("android:label", project.appName)

                file.activities.forEach { activity ->
                    println(activity)
                    "activity" {
                        attribute("android:name", ".${activity.fileName.toLowerCase().capitalize()}Activity")

                        // Ad intent the launcher intent filter if the current activity is MainActivity
                        if (activity.fileName == "main") {
                            "intent-filter" {
                                "action" {
                                    attribute("android:name", "android.intent.action.MAIN")
                                }

                                "category" {
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