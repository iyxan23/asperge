package com.iyxan23.asperge.generator.java.parser

import com.iyxan23.asperge.sketchware.models.projectfiles.logic.BlocksLogicSection
import java.lang.RuntimeException
import com.iyxan23.asperge.sketchware.models.projectfiles.logic.Block as LogicBlock

class BlocksParser(
    private val section: BlocksLogicSection
) {
    fun parse(): List<Block> {
        return section.blocks.values.map { block -> parseBlock(block) }
    }

    private val blacklistedIds = ArrayList<String>()

    private fun parseBlock(block: LogicBlock): Block {
        return Block(block, parseParams(block))
    }

    private fun parseParams(block: LogicBlock): List<Any> {
        return block.parameters.map { param ->
            if (param.startsWith("@")) {
                val paramBlockId = param.substring(1, param.length)
                blacklistedIds.add(paramBlockId)

                if (!section.blocks.containsKey(paramBlockId))
                    throw RuntimeException(
                        "This project is possibly corrupted, Trying to find the parameter of a block with the id " +
                        "of ${block.id} at ${section.name}.${section.contextName} but can't find one")

                return@map parseBlock(section.blocks[paramBlockId]!!)
            } else {

                return@map param
            }
        }
    }
}