package com.iyxan23.asperge.generator.java

import com.iyxan23.asperge.generator.java.builder.JavaCodeBuilder
import com.iyxan23.asperge.generator.java.builder.buildJavaCode
import com.iyxan23.asperge.sketchware.models.projectfiles.Project
import com.iyxan23.asperge.sketchware.models.projectfiles.logic.BaseLogicSection

class NewJavaGenerator(
    private val sections: List<BaseLogicSection>,
    private val viewIDs: List<String>,
    private val viewTypes: List<String>,
    project: Project
) {
    fun generate(): String {
        return buildJavaCode { TODO() }
    }
}