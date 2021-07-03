package com.iyxan23.asperge.generator.java

object BlocksDictionary {
    fun generateCode(opCode: String, parameters: List<String>): String {
        println("$opCode: $parameters")
        return when (opCode) {
            "setVarInt" -> "${parameters[0]} = ${parameters[1]};"
            "increaseInt" -> "${parameters[0]}++;"

            "addSourceDirectly" -> parameters[0]
            "finishActivity" -> "finishActivity();"

            else -> {
                "Unknown"
            }
        }
    }
}