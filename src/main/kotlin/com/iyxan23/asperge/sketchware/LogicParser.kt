package com.iyxan23.asperge.sketchware

import com.iyxan23.asperge.sketchware.models.Logic
import com.iyxan23.asperge.sketchware.models.logic.BaseLogicSection
import com.iyxan23.asperge.sketchware.models.logic.Variable
import com.iyxan23.asperge.sketchware.models.logic.VariablesLogicSection
import java.util.regex.Pattern

class LogicParser(content: String) {

    private var line = 0
    private var lines = content.lines()

    private var currentLine: String? = lines[line]

    fun advance() {
        line++
        currentLine = lines[line]
    }

    fun parse(): Logic {
        val sections = ArrayList<BaseLogicSection>()

        while (currentLine != null) {
            if (currentLine!!.startsWith("@"))
                sections.add(parseSection())

            advance()
        }

        return Logic(sections)
    }

    private fun parseSection(): BaseLogicSection {
        val splitHeaders = currentLine!!.substring(1, currentLine!!.length).split(".")

        val name = splitHeaders[0]
        val contextName = splitHeaders[1]

        advance()

        when (contextName) {
            "java_var" -> return VariablesLogicSection(name, contextName, parseVariables())
            "java_components" -> {

            }

            "java_events" -> {

            }

            "java_func" -> {

            }

            else -> {

            }
        }

        TODO()
    }

    private fun parseVariables(): List<Variable> {
        val result = ArrayList<Variable>()

        val pattern = Pattern.compile("([0-9]+):(\\w+)")
        var matcher = pattern.matcher(currentLine!!)

        while (matcher.find()) {
            result.add(Variable(matcher.group(0).toInt(), matcher.group(1)))

            advance()
            matcher = pattern.matcher(currentLine!!)
        }

        return result
    }
}