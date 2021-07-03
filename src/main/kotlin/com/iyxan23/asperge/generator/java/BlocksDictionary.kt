package com.iyxan23.asperge.generator.java

object BlocksDictionary {
    fun generateCode(opCode: String, parameters: List<String>, spec: String, addSemicolon: Boolean = true): String {
        println("$opCode: $parameters")

        if (opCode == "addSourceDirectly") return parameters[0]

        return (when (opCode) {
            "getVar" -> spec
            "toString" -> "${parameters[0]}.toString()"

            "setText" -> "${parameters[0]}.setText(${parameters[1]})"

            "setVarInt" -> "${parameters[0]} = ${parameters[1]}"
            "increaseInt" -> "${parameters[0]}++"

            "finishActivity" -> "finishActivity()"

            else -> "Unknown opcode $opCode"

        }) + if (addSemicolon) ";" else ""
    }
}