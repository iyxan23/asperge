package com.iyxan23.asperge.generator.java

object BlocksDictionary {
    fun generateCode(opCode: String, parameters: List<String>, spec: String, addSemicolon: Boolean = true): String {
        println("$opCode: $parameters")
        return when (opCode) {
            "getVar" -> spec                                                                                            + if (addSemicolon) ";" else ""
            "toString" -> "${parameters[0]}.toString()"                                                                 + if (addSemicolon) ";" else ""

            "setText" -> "${parameters[0]}.setText(${parameters[1]})"                                                   + if (addSemicolon) ";" else ""

            "setVarInt" -> "${parameters[0]} = ${parameters[1]}"                                                        + if (addSemicolon) ";" else ""
            "increaseInt" -> "${parameters[0]}++"                                                                       + if (addSemicolon) ";" else ""

            "addSourceDirectly" -> parameters[0]
            "finishActivity" -> "finishActivity()"                                                                      + if (addSemicolon) ";" else ""

            else -> {
                "Unknown"
            }
        }
    }
}