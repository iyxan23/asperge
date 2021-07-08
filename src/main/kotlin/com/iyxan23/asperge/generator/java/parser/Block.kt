package com.iyxan23.asperge.generator.java.parser

import com.iyxan23.asperge.sketchware.models.projectfiles.logic.Block as LogicBlock

data class Block(
    val logicBlock: LogicBlock,

    /**
     * Items in this List can only be either com.iyxan23.asperge.generator.java.parser.Block or String
     */
    val parameters: List<Any>,
)