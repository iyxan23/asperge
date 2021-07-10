package com.iyxan23.asperge.generator.java.parser

import com.iyxan23.asperge.sketchware.models.projectfiles.logic.Block as LogicBlock

data class Block(
    val logicBlock: LogicBlock,

    /**
     * Items in this List can only be either com.iyxan23.asperge.generator.java.parser.Block or String
     */
    val parameters: List<Any>,

    /**
     * Children of the first substack of this block, null if there is no substack1 for this block
     */
    val firstChildren: List<Block>? = null,

    /**
     * Children of the second substack of this block, null if there is no substack2 for this block
     */
    val secondChildren: List<Block>? = null,
)