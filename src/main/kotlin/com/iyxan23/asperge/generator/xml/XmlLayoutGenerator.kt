package com.iyxan23.asperge.generator.xml

import com.iyxan23.asperge.sketchware.models.projectfiles.File
import com.iyxan23.asperge.sketchware.models.projectfiles.Project
import com.iyxan23.asperge.sketchware.models.projectfiles.Resource
import com.iyxan23.asperge.sketchware.models.projectfiles.View
import com.iyxan23.asperge.sketchware.models.projectfiles.view.*
import org.redundent.kotlin.xml.Node
import org.redundent.kotlin.xml.parse
import org.redundent.kotlin.xml.xml

class XmlLayoutGenerator(
    private val layout: ViewSection,
    private val resource: Resource,
    private val file: File,
    private val project: Project,
) {

    val rootView = ViewNode(
        ViewItem(
            "",
            "",
            1.0f,
            0,
            0,
            1,
            "",
            1,
            1,
            1,
            "root",
            ImageConfig(null, 0, "CENTER"),
            "false",
            0,
            LayoutConfig(
                16777215,
                null,
                0,
                -2,
                0,
                0,
                0,
                0,
                0,
                1,
                8,
                8,
                8,
                8,
                0,
                0,
                -1
            ),
            100,
            null,
            12,
            null,
            "root",
            0,
            0,
            0,
            "?android:progressBarStyle",
            1.0f,
            1.0f,
            1,
            TextConfig(
                "",
                -10453621,
                0,
                1,
                0,
                0,
                "",
                -16777216,
                "default_font",
                12,
                0
            ),
            0.0f,
            0.0f,
            0
        ),
        ArrayList()
    )

    fun generate(): String {
        val parsedView = ViewsParser(layout).parse()
        rootView.childs.addAll(parsedView)
        return generateXml(rootView).toString(true)
    }

    private fun generateXml(node: ViewNode): Node {
        val viewName = getViewName(node.view.type)
        return xml(viewName) {
            attribute("android:id", "+@/${node.view.id}")

            if (node.view.enabled == 0) {
                attribute("android:enabled", "false")
            }

            when (viewName) {
                "LinearLayout" -> {
                    attribute("android:orientation", if (node.view.layout.orientation == 0) "horizontal" else "vertical")
                }

                "ScrollView" -> {
                    attribute("android:orientation", if (node.view.layout.orientation == 0) "horizontal" else "vertical")
                }

                "Button" -> {
                    attribute("android:text", node.view.text.text)
                    attribute("android:textSize", "${node.view.text.textSize}sp")
                }

                "TextView" -> {
                    attribute("android:text", node.view.text.text)
                    attribute("android:textSize", "${node.view.text.textSize}sp")
                }
            }

            for (child in node.childs) {
                addNode(generateXml(child))
            }
        }
    }

    private fun getViewName(type: Int): String {
        return when (type) {
            0 -> "LinearLayout"
            1 -> "Unknown"
            2 -> "ScrollView" // Horizontal
            3 -> "Button"
            4 -> "TextView"
            5 -> "EditText"
            6 -> "ImageView"
            8 -> "ProgressBar"
            9 -> "ListView"
            10 -> "Spinner"
            11 -> "CheckBox"
            12 -> "ScrollView" // Vertical
            13 -> "Switch"
            14 -> "SeekBar"
            15 -> "CalendarView"
            16 -> "Fab"
            17 -> "AdView"
            18 -> "MapView"
            else -> "Unknown"
        }
    }

    fun getViewIDs(): List<String> {
        return ArrayList<String>().apply { layout.views.forEach { add(it.id) } }
    }

    fun getViewTypes(): List<String> {
        return ArrayList<String>().apply { layout.views.forEach { add(getViewName(it.type)) } }
    }
}