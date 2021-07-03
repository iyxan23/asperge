package com.iyxan23.asperge.generator.java

import com.iyxan23.asperge.sketchware.models.projectfiles.Project
import com.iyxan23.asperge.sketchware.models.projectfiles.logic.*
import java.lang.StringBuilder

class JavaGenerator(
    val sections: List<BaseLogicSection>,
    project: Project
) {

    var variables = ""
    var onCreate = ""

    val initialTemplate =
"""package ${project.packageName};

import androidx.appcompat.app.AppCompatActivity;

public class %s extends AppCompatActivity {

%s

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
%s
    }
}
"""

    val globalVariables = ArrayList<Variable>()
    val events = ArrayList<Event>()
    val blocksSections = ArrayList<BlocksLogicSection>()

    var onCreateSection: BlocksLogicSection? = null

    fun generate(): String {
        var className = "MainActivity"

        sections.forEach { section ->
            className = section.name

            when (section) {
                is VariablesLogicSection -> {
                    globalVariables.addAll(section.variables)
                }

                is EventsLogicSection -> {
                    events.addAll(section.events)
                }

                is BlocksLogicSection -> {
                    if (section.contextName == "java_onCreate_initializeLogic") {
                        onCreateSection = section
                    } else {
                        blocksSections.add(section)
                    }
                }
            }
        }

        globalVariables.forEach {
            variables += "\nprivate ${genVariableDeclaration(it.type, it.name)}"
        }

        variables = variables.trim().prependIndent(" ".repeat(4))

        blocksSections.forEach {
            println("${it.name} ${it.contextName}")
        }

        return initialTemplate.format(className, variables, generateCode(blocksSections[0]))
    }

    // Used to blacklist blocks that is parsed as a parameter
    private val blacklistedBlocks = ArrayList<String>()

    private fun generateCode(section: BlocksLogicSection, idOffset: Int = 0, addSemicolon: Boolean = true): String {
        val result = StringBuilder()

        section.blocks.values.forEach { block ->
            if (block.id.toInt() < idOffset) return@forEach
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

    private fun genVariableDeclaration(type: Int, name: String): String {
        return when (type) {
            0 -> "boolean $name = false;"
            1 -> "int $name = 0;"
            2 -> "String $name = \"\";"
            else -> "Unknown Type $type"
        }
    }
}