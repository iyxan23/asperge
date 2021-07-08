package com.iyxan23.asperge.generator.java

import com.iyxan23.asperge.generator.java.builder.buildJavaCode
import com.iyxan23.asperge.generator.java.parser.Block
import com.iyxan23.asperge.generator.java.parser.BlocksParser
import com.iyxan23.asperge.sketchware.models.projectfiles.Project
import com.iyxan23.asperge.sketchware.models.projectfiles.logic.*

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
                addSpace()
            }

            // Add list variables
            if (lists.isNotEmpty()) {
                addCode("// List variable(s)")
                lists.forEach { listVariable -> addCode(listDeclaration(listVariable.type, listVariable.name)) }
                addSpace()
            }

            // Add view variables
            if (viewIDs.isNotEmpty()) {
                addCode("// View declaration(s)")
                viewIDs.forEachIndexed { index, id -> addCode("${viewTypes[index]} $id;") }
                addSpace()
            }

            onCreate {
                addCode("super.onCreate(savedInstanceState);")
                addCode("setContentView(R.layout.${layoutName})")
                addSpace()

                addCode("initializeViews();")
                addSpace()

                addCode(generateCode(onCreateSection!!.blocks))
            }

            function("private void", "registerEvents") {
                events.forEach { event -> addCode(generateCodeFromEvent(event)) }
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

    private fun generateCode(rawBlocks: LinkedHashMap<String, com.iyxan23.asperge.sketchware.models.projectfiles.logic.Block>): String {
        // First, we need to parse the section
        val blocks = BlocksParser(rawBlocks).parse()

        return StringBuilder().apply {
            blocks.forEach { block -> appendLine(generateCodeFromBlock(block)) }
        }.toString().trim()
    }

    private fun generateCodeFromBlock(block: Block, addSemicolon: Boolean = true): String {
        val parameters = generateParametersCode(block.parameters)

        return BlocksDictionary.generateCode(
            block.logicBlock.opCode,
            parameters,
            block.logicBlock.spec,
            addSemicolon
        )
    }

    private fun generateParametersCode(params: List<Any>): List<String> {
        return params.map { param ->
            when (param) {
                is String -> param

                is Block ->
                    BlocksDictionary.generateCode(
                        param.logicBlock.opCode,
                        generateParametersCode(param.parameters),
                        param.logicBlock.spec,
                        addSemicolon = false // This is a parameter, we can't have semicolons at the end of the code
                    )

                else -> throw RuntimeException("Unexpected type ${param.javaClass.name} while parsing parameters")
            }
        }
    }

    private fun generateCodeFromEvent(event: Event): String {
        val blocks = eventsBlocks["java_${event.targetId}_${event.eventName}"]
            ?: return "// Cannot find blocks of $event".prependIndent(" ".repeat(8))

        return EventsDictionary.generateCode(
            event,
            generateCode(blocks.blocks) // <- the blocks of this event
        )
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