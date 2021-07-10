package com.iyxan23.asperge.generator.java

import java.util.regex.Pattern

object BlocksDictionary {
    fun generateCode(
        opCode: String,
        parameters: List<String>,
        spec: String,
        firstSubstack: String = "",
        secondSubstack: String = "",
        addSemicolon: Boolean = true
    ): String {
        if (opCode == "addSourceDirectly") return parameters[0]
        var addSemicolonMutable = addSemicolon

        return (when (opCode) {
            "if" -> {
                addSemicolonMutable = false
                "if (${parameters[0]}) {\n${firstSubstack.prependIndent(" ".repeat(4))}\n}"
            }

            "ifElse" -> {
                addSemicolonMutable = false
                "if (${parameters[0]}) {\n${firstSubstack.prependIndent(" ".repeat(4))}\n} else {\n${secondSubstack.prependIndent(" ".repeat(4))}\n}"
            }

            "true" -> "true"
            "false" -> "false"

            "getVar" -> spec
            "toString" -> "${parameters[0]}.toString()"

            "setText" -> "${parameters[0]}.setText(${parameters[1]})"

            "setVarInt" -> "${parameters[0]} = ${parameters[1]}"
            "increaseInt" -> "${parameters[0]}++"

            "finishActivity" -> "finishActivity()"

            "definedFunc" ->  "${spec.split(" ")[0]}(${functionParameters(spec, parameters)})"

            else -> "// Unknown opcode $opCode"

        }) + if (addSemicolonMutable) ";" else ""
    }

    // Pretty similar to NewJavaGenerator#functionParameters, but this thing only checks if the parameter is a string
    // and if it is, add "" surrounding it
    private fun functionParameters(spec: String, parameters: List<String>): String {
        val matcher = Pattern.compile("%(\\w)\\.(\\w+)[.(\\w+)]?").matcher(spec)

        return ArrayList<String>().apply {
            var index = 0
            while (matcher.find()) {
                val type = matcher.group(1)

                if (type == "s") {
                    add("\"${parameters[index]}\"")
                } else {
                    add(parameters[index])
                }

                index++
            }
        }.joinToString(", ")
    }
}