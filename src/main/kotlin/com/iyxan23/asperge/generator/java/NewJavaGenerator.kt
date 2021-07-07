package com.iyxan23.asperge.generator.java

import com.iyxan23.asperge.generator.java.builder.buildJavaCode
import com.iyxan23.asperge.sketchware.models.projectfiles.Project
import com.iyxan23.asperge.sketchware.models.projectfiles.logic.*
import java.lang.StringBuilder

class NewJavaGenerator(
    private val sections: List<BaseLogicSection>, // MUST be from the same activity
    private val viewIDs: List<String>,
    private val viewTypes: List<String>,
    private val activityName: String,
    private val layoutName: String,
    private val project: Project
) {

    private val globalVariables = ArrayList<Variable>()

    private val events = ArrayList<Event>()
    private val eventsBlocks = HashMap<String, BlocksLogicSection>()

    private val components = ArrayList<Component>()
    private val lists = ArrayList<ListLogic>()

    private var onCreateSection: BlocksLogicSection? = null

    fun generate(): String {
        sortSections()

        return buildJavaCode(
            "public",
            activityName,
            "extends AppCompatActivity",
            project.packageName
        ) {
            addImport("androidx.appcompat.app.AppCompatActivity")
            addImport("android.view.*")

            // Add global variables
            if (globalVariables.isNotEmpty()) {
                addCode("// Global variable(s)")
                globalVariables.forEach { variable -> addCode(varDeclaration(variable.type, variable.name)) }
            }

            // Add list variables
            if (lists.isNotEmpty()) {
                addCode("// List variable(s)")
                lists.forEach { listVariable -> addCode(listDeclaration(listVariable.type, listVariable.name)) }
            }

            // Add view variables
            if (viewIDs.isNotEmpty()) {
                addCode("// View declaration(s)")
                viewIDs.forEachIndexed { index, id -> addCode("${viewTypes[index]} $id;") }
            }

            onCreate {
                addCode("super.onCreate(savedInstanceState);")
                addCode("setContentView(R.layout.${layoutName})")

                addCode("initializeViews();")

                addCode(generateCode(onCreateSection!!))
            }

            function("private void", "initializeViews()") {
                viewIDs.forEach { view -> addCode("$view = findViewById(R.id.$view)") }
            }
        }
    }

    private fun sortSections() {
        sections.forEach { section ->
            when (section) {
                is VariablesLogicSection    -> globalVariables.addAll(section.variables)
                is EventsLogicSection       -> events.addAll(section.events)
                is ComponentsLogicSection   -> components.addAll(section.components)
                is ListLogicSection         -> lists.addAll(section.lists)

                is BlocksLogicSection -> {
                    if (section.contextName == "java_onCreate_initializeLogic") {
                        onCreateSection = section
                    } else {
                        eventsBlocks[section.contextName] = section
                    }
                }
            }
        }
    }

    // Used to blacklist blocks that is parsed as a parameter
    private val blacklistedBlocks = ArrayList<String>()

    private fun generateCode(section: BlocksLogicSection, idOffset: Int = -1, addSemicolon: Boolean = true): String {
        val result = StringBuilder()
        var skipOffset = idOffset != -1

        section.blocks.values.forEach { block ->
            if (skipOffset) {
                if (block.id.toInt() == idOffset) skipOffset = false
                else return@forEach
            }

            if (blacklistedBlocks.contains(block.id)) return@forEach

            val parsedParams = ArrayList<String>()

            block.parameters.forEach { param ->
                if (param.startsWith("@")) {
                    // this is a block parameter
                    val blockId = param.substring(1, param.length)
                    parsedParams.add(generateCode(section, blockId.toInt(), false))

                    blacklistedBlocks.add(blockId)

                } else {
                    parsedParams.add(param)
                }
            }

            result.appendLine(BlocksDictionary.generateCode(block.opCode, parsedParams, block.spec, addSemicolon))
        }

        return result.toString().trim()
    }

    private fun varDeclaration(type: Int, name: String): String {
        return when (type) {
            0 -> "boolean $name = false;"
            1 -> "int $name = 0;"
            2 -> "String $name = \"\";"
            3 -> "HashMap<String, Object> $name = new HashMap();"
            else -> "// Unknown variable type $type. Variable name: $name"
        }
    }

    private fun listDeclaration(type: Int, name: String): String {
        return when (type) {
            0 -> "ArrayList<Boolean> $name = new ArrayList();"
            1 -> "ArrayList<Integer> $name = new ArrayList();"
            2 -> "ArrayList<String> $name = new ArrayList();"
            3 -> "ArrayList<HashMap<String, Object>> $name = new ArrayList();"
            else -> "// Unknown variable type $type. Variable name: $name"
        }
    }
}