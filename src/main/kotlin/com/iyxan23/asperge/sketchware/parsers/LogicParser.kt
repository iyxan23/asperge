package com.iyxan23.asperge.sketchware.parsers

import com.iyxan23.asperge.sketchware.models.projectfiles.Logic
import com.iyxan23.asperge.sketchware.models.projectfiles.logic.*
import com.iyxan23.asperge.sketchware.models.projectfiles.logic.Function
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.regex.Pattern

class LogicParser(content: String) : Parser<Logic>(content) {

    override fun parse(): Logic {
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

        return when (contextName) {
            "java_var"          -> VariablesLogicSection    (name, contextName, parseVariables())
            "java_list"         -> ListLogicSection         (name, contextName, parseLists())
            "java_components"   -> ComponentsLogicSection   (name, contextName, parseSerializable())
            "java_events"       -> EventsLogicSection       (name, contextName, parseSerializable())
            "java_func"         -> FunctionsLogicSection    (name, contextName, parseFunctions())
            else                -> BlocksLogicSection       (name, contextName, parseSerializable())
        }
    }

    private fun parseVariables(): List<Variable> {
        val result = ArrayList<Variable>()

        val pattern = Pattern.compile("([0-9]+):(\\w+)")
        var matcher = pattern.matcher(currentLine!!)

        while (matcher.find()) {
            result.add(Variable(matcher.group(1).toInt(), matcher.group(2)))

            advance()
            matcher = pattern.matcher(currentLine!!)
        }

        return result
    }

    private fun parseLists(): List<ListLogic> {
        val result = ArrayList<ListLogic>()

        val pattern = Pattern.compile("([0-9]+):(\\w+)")
        var matcher = pattern.matcher(currentLine!!)

        while (matcher.find()) {
            result.add(ListLogic(matcher.group(1).toInt(), matcher.group(2)))

            advance()
            matcher = pattern.matcher(currentLine!!)
        }

        return result
    }

    private fun parseFunctions(): List<Function> {
        val result = ArrayList<Function>()

        val pattern = Pattern.compile("(\\w+):(.+)")
        var matcher = pattern.matcher(currentLine!!)

        while (matcher.find()) {
            result.add(Function(matcher.group(1), matcher.group(2)))

            advance()
            matcher = pattern.matcher(currentLine!!)
        }

        return result
    }

    // Used to parse serializable objects, such as Component, Event, etc
    private inline fun <reified T> parseSerializable(): List<T> {
        val result = ArrayList<T>()

        while (currentLine!!.trim().isNotEmpty()) {
            result.add(Json.decodeFromString(currentLine!!))

            advance()
        }

        return result
    }
}