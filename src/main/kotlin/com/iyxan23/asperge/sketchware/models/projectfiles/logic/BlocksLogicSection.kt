package com.iyxan23.asperge.sketchware.models.projectfiles.logic

class BlocksLogicSection(
    name: String,
    contextName: String,

    // HashMap of <blockId: String, block: Block>
    val blocks: HashMap<String, Block>

) : BaseLogicSection(name, contextName)