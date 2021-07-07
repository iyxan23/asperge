package com.iyxan23.asperge.generator.java

import com.iyxan23.asperge.generator.java.builder.buildJavaCode
import com.iyxan23.asperge.sketchware.models.projectfiles.Project
import com.iyxan23.asperge.sketchware.models.projectfiles.file.FileItem
import com.iyxan23.asperge.sketchware.models.projectfiles.logic.*

class NewJavaGenerator(
    private val sections: List<BaseLogicSection>,
    private val viewIDs: List<String>,
    private val viewTypes: List<String>,
    private val activityData: FileItem,
    private val project: Project
) {

    private val globalVariables = ArrayList<Variable>()
    private var className: String = activityData.fileName.capitalize() + "Activity"

    private val events = ArrayList<Event>()
    private val eventsBlocks = HashMap<String, BlocksLogicSection>()

    private val components = ArrayList<Component>()
    private val lists = ArrayList<ListLogic>()

    private var onCreateSection: BlocksLogicSection? = null

    fun generate(): String {
        sortSections()

        return buildJavaCode(
            "public",
            className,
            "extends AppCompatActivity",
            "com.test.helloworld"
        ) {
            addImport("androidx.appcompat.app.AppCompatActivity")
            addImport("android.view.*")

            // Add global variables
            if (globalVariables.size > 0) {
                addCode("// Global variable(s)")
                globalVariables.forEach { variable -> addCode(varDeclaration(variable.type, variable.name)) }
            }

            // Add list variables
            if (globalVariables.size > 0) {
                addCode("// List variable(s)")
                lists.forEach { listVariable -> addCode(listDeclaration(listVariable.type, listVariable.name)) }
            }

            onCreate {
                addCode("super.onCreate(savedInstanceState);")
                addCode("setContentView(R.layout.${activityData.fileName})")
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