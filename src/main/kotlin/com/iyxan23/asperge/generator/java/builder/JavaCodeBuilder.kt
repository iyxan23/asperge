package com.iyxan23.asperge.generator.java.builder

fun buildJavaCode(builder: JavaCodeBuilder.() -> Unit): String
    = JavaCodeBuilder().apply { builder.invoke(this) }.generate()

// A class that builds java code with a very pretty syntax
class JavaCodeBuilder() {

    private val neededImports = HashSet<String>().apply { add("androidx.appcompat.app.AppCompatActivity"); add("android.view.*") }
    private var classHeader = ""
    private var classFooter = ""
    private var onCreateCode = ""

    fun addImport(import: String) = neededImports.add(import)

    ////////

    fun classHeader(builder: OuterCodeBuilder.() -> Unit) {
        val code = OuterCodeBuilder().apply { builder.invoke(this) }.code
        classHeader += code + "\n"
    }

    fun onCreate(builder: CodeBuilder.() -> Unit) {
        val code = CodeBuilder().apply { builder.invoke(this) }.code
        onCreateCode += code + "\n"
    }

    fun classFooter(builder: OuterCodeBuilder.() -> Unit) {
        val code = OuterCodeBuilder().apply { builder.invoke(this) }.code
        classFooter += code + "\n"
    }

    open inner class CodeBuilder {
        var code = ""

        fun addCode(code: String) {
            this.code += code
        }
    }

    inner class OuterCodeBuilder : CodeBuilder() {
        fun function(modifiers: String, name: String, parameters: List<String>, builder: CodeBuilder.() -> Unit) {
            code += "\n$modifiers $name(${ parameters.joinToString(", ") }) {"
            code += CodeBuilder().apply { builder.invoke(this) }.code
            code += "\n}\n"
        }
    }

    /////

    fun generate(): String {
        return TODO()
    }
}