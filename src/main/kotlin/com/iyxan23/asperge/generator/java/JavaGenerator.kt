package com.iyxan23.asperge.generator.java

import com.iyxan23.asperge.sketchware.models.projectfiles.Project
import com.iyxan23.asperge.sketchware.models.projectfiles.logic.*

class JavaGenerator(
    val sections: List<BaseLogicSection>,
    project: Project
) {

    val initialTemplate = """
package ${project.packageName};

import androidx.appcompat.app.AppCompatActivity;

public class %s extends AppCompatActivity {
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

        return initialTemplate.format(className, "// Hello World")
    }
}