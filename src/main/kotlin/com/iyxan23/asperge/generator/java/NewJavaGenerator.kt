package com.iyxan23.asperge.generator.java

import com.iyxan23.asperge.generator.java.builder.buildJavaCode
import com.iyxan23.asperge.generator.java.parser.Block
import com.iyxan23.asperge.generator.java.parser.BlocksParser
import com.iyxan23.asperge.sketchware.models.projectfiles.Project
import com.iyxan23.asperge.sketchware.models.projectfiles.logic.*
import com.iyxan23.asperge.sketchware.models.projectfiles.logic.Function
import java.util.regex.Pattern

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

    private val functions = ArrayList<Function>()
    private val functionsBlocks = HashMap<String, BlocksLogicSection>()

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

                if (viewIDs.isNotEmpty()) addCode("initializeViews();")
                if (events.isNotEmpty()) addCode("registerEvents();")
                addSpace()

                addCode(generateCode(onCreateSection!!.blocks))
            }

            if (events.isNotEmpty()) {
                function("private void", "registerEvents") {
                    events.forEach { event -> addCode(generateCodeFromEvent(event)) }
                }
            }

            if (viewIDs.isNotEmpty()) {
                function("private void", "initializeViews()") {
                    viewIDs.forEach { view -> addCode("$view = findViewById(R.id.$view)") }
                }
            }

            addCode("// Moreblocks")
            functions.forEach {
                val blocks = functionsBlocks[it.name]
                    ?: /* if we can't find the blocks then this moreblock is empty */ return@forEach

                function("private void", "${it.name}(${functionParameters(it.spec)})") {
                    addCode(generateCode(blocks.blocks))
                }
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
                is FunctionsLogicSection    -> functions.addAll(section.functions)

                is BlocksLogicSection -> {
                    when {
                        section.contextName == "java_onCreate_initializeLogic" ->
                            onCreateSection = section

                        section.contextName.endsWith("_moreBlock") -> {
                            val functionName = section.contextName
                                .removePrefix("java_")
                                .removeSuffix("_moreBlock")

                            functionsBlocks[functionName] = section
                        }

                        else -> eventsBlocks[section.contextName] = section
                    }
                }
            }
        }
    }

    private fun generateCode(rawBlocks: LinkedHashMap<String, com.iyxan23.asperge.sketchware.models.projectfiles.logic.Block>): String {
        // First, we need to parse the section
        val blocks = BlocksParser(rawBlocks).parse()

        return generateCode(blocks)
    }

    private fun generateCode(blocks: List<Block>): String {
        return StringBuilder().apply {
            blocks.forEach { block -> appendLine(generateCodeFromBlock(block)) }
        }.toString().trim()
    }

    private fun generateCodeFromBlock(block: Block, addSemicolon: Boolean = true): String {
        val parameters = generateParametersCode(block.parameters)

        var firstSubstack = ""
        var secondSubstack = ""

        // Check if these substack(s) exists
        if (block.firstChildren != null)
            firstSubstack = generateCode(block.firstChildren)

        if (block.secondChildren != null)
            secondSubstack = generateCode(block.secondChildren)

        return BlocksDictionary.generateCode(
            block.logicBlock.opCode,
            parameters,
            block.logicBlock.spec,
            firstSubstack,
            secondSubstack,
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

    private fun functionParameters(spec: String): String {
        /* %(type).(name1)[.(name2)]
         * if type is "m" then name = name2
         * else name = name1
         *
         * example:
         * myMoreblock %b.myBoolean %s.myString %m.textview.aTextView %m.file.fileComponent
         */

        val matcher = Pattern.compile("%(\\w)\\.(\\w+)[.(\\w+)]?").matcher(spec)

        return ArrayList<String>().apply {
            while (matcher.find()) {
                val type = matcher.group(1)
                val name = matcher.group(if (type != "m") 2 else 3)

                if (type != "m") {
                    add("${parameterType(type)} _$name")
                } else {
                    add("${extParameterType(matcher.group(2))} _$name")
                }
            }
        }.joinToString(", ")
    }

    private fun parameterType(type: String): String {
        return when (type) {
            "b" -> "boolean"
            "d" -> "int"
            "s" -> "String"
            else -> "/* Unknown type $type */"
        }
    }

    // the %m parameter
    private fun extParameterType(type: String): String {
        return when (type) {
            "varMap" -> "HashMap<String>"
            "listStr" -> "ArrayList<String>"
            "listInt" -> "ArrayList<Integer>"
            "listMap" -> "ArrayList<HashMap<String, Object>>"
            // TODO: 7/9/21 add more of these
            else -> "/* Unknown type $type */"
        }
    }
}