package com.iyxan23.asperge.sketchware.models.projectfiles.logic

class ComponentsLogicSection(
    name: String,
    contextName: String,

    val components: List<Component>
) : BaseLogicSection(name, contextName)