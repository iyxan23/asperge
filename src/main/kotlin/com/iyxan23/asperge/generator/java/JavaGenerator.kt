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
                    blocksSections.add(section)
                }
            }
        }

        globalVariables.forEach {
            variables += "\nprivate ${getVarType(it.type)} ${it.name};"
        }

        variables = variables.trim().prependIndent(" ".repeat(4))

        return initialTemplate.format(className, variables, generateCode(blocksSections[0]))
    }

    private fun generateCode(section: BlocksLogicSection, idOffset: Int = 0): String {
        val result = StringBuilder()

        section.blocks.values.forEach { block ->
            if (block.id.toInt() < idOffset) return@forEach

            val parsedParams = ArrayList<String>()

            block.parameters.forEach { param ->
                if (param.startsWith("@")) {
                    // this is a block parameter
                    val blockId = param.substring(1, param.length)
                    parsedParams.add(generateCode(section, blockId.toInt()))

                } else {
                    parsedParams.add(param)
                }
            }

            result.appendLine(BlocksDictionary.generateCode(block.opCode, parsedParams))
        }

        return result.toString().trim().prependIndent(" ".repeat(8))
    }

    private fun getVarType(type: Int): String {
        return when (type) {
            0 -> "boolean"
            1 -> "int"
            2 -> "String"
            else -> "Unknown Type $type"
        }
    }
}