package com.iyxan23.asperge.generator.xml

import com.iyxan23.asperge.sketchware.models.projectfiles.File
import com.iyxan23.asperge.sketchware.models.projectfiles.Project
import com.iyxan23.asperge.sketchware.models.projectfiles.Resource
import com.iyxan23.asperge.sketchware.models.projectfiles.view.*
import org.redundent.kotlin.xml.Node
import org.redundent.kotlin.xml.xml

class XmlLayoutGenerator(
    private val layout: ViewSection,
    private val resource: Resource,
    private val file: File,
    private val project: Project,
) {

    private val rootView = ViewNode(
        ViewItem(
            "", //
            "", //
            1.0f,
            0,
            0, //
            1,
            "", //
            1, //
            1,
            1, //
            "root",
            ImageConfig(null, 0, "CENTER"),
            "false",
            0, //
            LayoutConfig(
                16777215,
                null,
                0, //
                -1,
                0, //
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
            12, //
            null, //
            "root", //
            0, //
            0, //
            0,
            "?android:progressBarStyle",
            1.0f,
            1.0f,
            1, //
            TextConfig(
                "",
                -10453621,
                0, //
                1, //
                0,
                0,
                "",
                -16777216,
                "default_font",
                12,
                0 //
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

        return generateXml(rootView, addNamespaces = true).toString(true)
    }

    private fun generateXml(node: ViewNode, addNamespaces: Boolean = false): Node {
        val viewName = getViewName(node.view.type)
        val view = node.view

        return xml(viewName) {
            if (addNamespaces) {
                attribute("xmlns:android", "http://schemas.android.com/apk/res/android")
                attribute("xmlns:app", "http://schemas.android.com/apk/res-auto")
                attribute("xmlns:tools", "http://schemas.android.com/tools")
            }

            attribute("android:id", "@+id/${view.id}")

            attribute("android:layout_height", resolveLayoutValue(view.layout.height))
            attribute("android:layout_width", resolveLayoutValue(view.layout.width))

            when (viewName) {
                "LinearLayout" ->
                    attribute("android:orientation", if (view.layout.orientation == 0) "horizontal" else "vertical")

                "ScrollView" ->
                    attribute("android:orientation", if (view.layout.orientation == 0) "horizontal" else "vertical")

                "Button" -> {
                    attribute("android:text", view.text.text)
                    attribute("android:textSize", "${view.text.textSize}sp")
                    attribute("android:textColor", "#%X".format(view.text.textColor))
                }

                "TextView" -> {
                    attribute("android:text", view.text.text)
                    attribute("android:textSize", "${view.text.textSize}sp")

                    if (view.text.line != 1)
                        attribute("android:lines", view.text.line.toString())

                    if (view.text.singleLine != 0)
                        attribute("android:singleLine", "true")

                    if (view.text.textColor != -16777216)
                        attribute("android:textColor", "#%06X".format(view.text.textColor))

                    if (view.text.textFont != "default_font")
                        attribute("android:fontFamily", view.text.textFont)
                }

                "ImageView" -> {
                    if (view.image.resName != null) attribute("android:src", view.image.resName)
                    attribute("android:scaleType", underscoresToCapital(view.image.scaleType))
                }

                "ProgressBar" -> {
                    if (view.indeterminate == "false") {
                        attribute("android:max", view.max.toString())
                        attribute("android:progress", view.progress.toString())
                    }

                    if (view.progressStyle != "?android:progressBarStyle")
                        attribute("style", view.progressStyle)
                }

                "EditText" -> {
                    attribute("android:textSize", "${view.text.textSize}sp")

                    if (view.text.hint != "")
                        attribute("android:hint", view.text.hint)

                    if (view.text.hintColor != -10453621)
                        attribute("android:textColorHint", "#%06X".format(view.text.hintColor))

                    if (view.text.line != 1)
                        attribute("android:lines", view.text.line.toString())

                    if (view.text.singleLine != 0)
                        attribute("android:singleLine", "true")

                    if (view.text.textColor != -16777216)
                        attribute("android:textColor", "#%06X".format(view.text.textColor))

                    if (view.text.textFont != "default_font")
                        attribute("android:fontFamily", view.text.textFont)
                }
            }

            if (view.scaleX != 1f) attribute("android:scaleX", "${view.scaleX}")
            if (view.scaleY != 1f) attribute("android:scaleY", "${view.scaleY}")
            if (view.enabled == 0) attribute("android:enabled", "false")
            if (view.alpha != 1.0f) attribute("android:alpha", "${view.alpha}")
            if (view.checked != 0) attribute("android:checked", "true")
            if (view.clickable != 1) attribute("android:clickable", "false")
            if (view.enabled != 1) attribute("android:enabled", "false")
            if (view.image.rotate != 0) attribute("android:rotation", "${view.image.rotate}")
            if (view.indeterminate != "false") attribute("android:indeterminate", "true")

            if (view.layout.backgroundColor != 16777215) attribute("android:backgroundColor", "#%06X".format(view.layout.backgroundColor))
            if (view.layout.backgroundResource != null) attribute("android:backgroundResource", "${view.layout.backgroundResource}")

            if (areAllEqual(view.layout.marginTop, view.layout.marginBottom, view.layout.marginRight, view.layout.marginLeft)) {
                if (view.layout.marginTop != 0) attribute("android:margin", "${view.layout.marginTop}dp")
            } else {
                if (view.layout.marginTop != 0) attribute("android:marginTop", "${view.layout.marginTop}dp")
                if (view.layout.marginRight != 0) attribute("android:marginRight", "${view.layout.marginRight}dp")
                if (view.layout.marginBottom != 0) attribute("android:marginBottom", "${view.layout.marginBottom}dp")
                if (view.layout.marginLeft != 0) attribute("android:marginLeft", "${view.layout.marginLeft}dp")
            }

            if (areAllEqual(view.layout.paddingTop, view.layout.paddingBottom, view.layout.paddingRight, view.layout.paddingLeft)) {
                if (view.layout.paddingTop != 0) attribute("android:padding", "${view.layout.paddingTop}dp")
            } else {
                if (view.layout.paddingTop != 0) attribute("android:paddingTop", "${view.layout.paddingTop}dp")
                if (view.layout.paddingRight != 0) attribute("android:paddingRight", "${view.layout.paddingRight}dp")
                if (view.layout.paddingBottom != 0) attribute("android:paddingBottom", "${view.layout.paddingBottom}dp")
                if (view.layout.paddingLeft != 0) attribute("android:paddingLeft", "${view.layout.paddingLeft}dp")
            }

            if (view.layout.weight != 0) attribute("android:weight", "${view.layout.weight}")
            if (view.layout.weightSum != 0) attribute("android:weightSum", "${view.layout.weightSum}")

            if (view.translationX != 1f) attribute("android:scaleX", "${view.translationX}")
            if (view.translationY != 1f) attribute("android:scaleY", "${view.translationY}")

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

    private fun resolveLayoutValue(value: Int): String {
        return when (value) {
            -1 -> "match_parent"
            -2 -> "wrap_content"
            else -> "${value}dp"
        }
    }

    fun getViewIDs(): List<String> {
        return ArrayList<String>().apply { layout.views.forEach { add(it.id) } }
    }

    fun getViewTypes(): List<String> {
        return ArrayList<String>().apply { layout.views.forEach { add(getViewName(it.type)) } }
    }

    private fun underscoresToCapital(string: String): String {
        var capitalize = false
        return StringBuilder().apply {
            string.toLowerCase().forEach { char ->
                when {
                    capitalize -> {
                        append(char.toUpperCase())
                        capitalize = false
                    }

                    char == '_' -> capitalize = true
                    else -> append(char)
                }
            }
        }.toString()
    }

    private fun areAllEqual(vararg values: Int): Boolean {
        if (values.isEmpty()) return true

        val checkValue = values[0]
        values.forEach { value -> if (value != checkValue) return false }

        return true
    }
}