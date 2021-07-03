package com.iyxan23.asperge.generator.java

object BlocksDictionary {
    fun generateCode(opCode: String, parameters: List<String>): String {
        when (opCode) {
            "setVarInt" -> return "${parameters[0]} = ${parameters[1]};"
            "increaseInt" -> return "${parameters[0]}++;"

            else -> {
                return "Unknown"
            }
        }
    }
}