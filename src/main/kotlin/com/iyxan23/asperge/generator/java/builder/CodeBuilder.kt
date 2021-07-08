package com.iyxan23.asperge.generator.java.builder

open class CodeBuilder {
    open var code = ""

    fun addCode(code: String) {
        this.code += "$code\n"
    }

    fun addSpace() {
        this.code += "\n"
    }
}