package com.iyxan23.asperge.generator.java.builder

fun buildJavaCode(classModifiers: String,
                  className: String,
                  classEnd: String = "",
                  packageName: String? = null,
                  builder: JavaClassBuilder.() -> Unit
): String
    = JavaClassBuilder(classModifiers, className, classEnd, packageName).apply { builder.invoke(this) }.generate()

// A class that builds java code with a very pretty syntax
class JavaClassBuilder(
    classModifiers: String,

    private val className: String,

    classEnd: String = "",
    packageName: String? = null,

    private val indentationAmount: Int = 4,
    private val indentation: Int = 4,
) : CodeBuilder() {

    private val neededImports = HashSet<String>().apply { add("androidx.appcompat.app.AppCompatActivity"); add("android.view.*") }
    private var codeHeader =
"""
${ if (packageName != null) "package $packageName;" else "" }

%s

$classModifiers class $className $classEnd {
""".trim() + "\n"

    override var code = ""

    ////////

    fun addImport(import: String) = neededImports.add(import)

    ////////

    fun constructor(modifiers: String, parameters: String, builder: CodeBuilder.() -> Unit) {
        function(modifiers, "$className($parameters)", builder)
    }

    fun onCreate(builder: CodeBuilder.() -> Unit) {
        function("@Override\npublic void", "onCreate(Bundle savedInstanceState)", builder)
    }

    fun function(modifiers: String, name: String, builder: CodeBuilder.() -> Unit) {
        var functionCode = ""

        functionCode += "$modifiers $name {\n"
        functionCode += CodeBuilder().apply { builder.invoke(this) }.code.trim().prependIndent(" ".repeat(indentationAmount))
        functionCode += "\n}\n\n"

        code += functionCode
    }

    fun createClass(modifiers: String, name: String, classEnd: String = "", builder: JavaClassBuilder.() -> Unit) {
        val builderInst = JavaClassBuilder(
            modifiers,
            name,
            classEnd,
            indentation = indentationAmount,
            indentationAmount = indentationAmount
        )

        builder.invoke(builderInst)

        // Add the class code but without the imports, propagate the imports up to our class instead
        code += builderInst.generate(withImports = false) + "\n\n"
        neededImports.addAll(builderInst.neededImports)
    }

    /////

    fun generate(withImports: Boolean = true): String {
        // Check if we need to add imports
        // Note: Imports are inserted using the format technique
        codeHeader = if (withImports) {
            codeHeader.format(
                StringBuilder().apply {
                    neededImports.forEach {
                        appendLine("import $it;")
                    }
                }.toString().trim()
            ).trimStart()
        } else {
            codeHeader.format("").trimStart()
        }

        return codeHeader + code.prependIndent(" ".repeat(indentation)).trimEnd() + "\n}"
    }
}