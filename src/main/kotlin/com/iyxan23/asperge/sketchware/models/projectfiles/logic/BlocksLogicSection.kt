package com.iyxan23.asperge.sketchware.models.projectfiles.logic

class BlocksLogicSection(
    name: String,
    contextName: String,

    // Note: I use LinkedHashMap to retain the order of insertion
    // HashMap of <blockId: String, block: Block>
    val blocks: LinkedHashMap<String, Block>

) : BaseLogicSection(name, contextName)