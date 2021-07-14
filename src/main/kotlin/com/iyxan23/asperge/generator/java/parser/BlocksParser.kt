package com.iyxan23.asperge.generator.java.parser

import com.iyxan23.asperge.sketchware.models.projectfiles.logic.Block as LogicBlock

class BlocksParser(
    private val rawBlocks: LinkedHashMap<String, LogicBlock>
) {
    private var index = 0
    private var blocks = rawBlocks.values.toList()

    private var currentBlock: LogicBlock? = blocks[index]

    private val blacklistedIds = ArrayList<String>()

    private fun advance() {
        do {
            index++
            currentBlock = if (index >= blocks.size) null else blocks[index]

        } while (currentBlock?.let { return@let blacklistedIds.contains(currentBlock!!.id) } == true)
    }

    fun parse(): List<Block> {
        return parseBlocks()
    }

    private fun parseBlocks(): List<Block> {
        val result = ArrayList<Block>()

        while (currentBlock != null) {
            // we need to keep the reference of the "current" block
            val block = currentBlock!!.copy()
            result.add(parseBlock(block))
            if (block.nextBlock == -1) break

            advance()
        }

        return result
    }

    private fun parseBlock(block: LogicBlock): Block {
        var firstChildren: List<Block>? = null
        var secondChildren: List<Block>? = null
        val params = parseParams(block)

        if (block.subStack1 != -1) {
            advance()
            firstChildren = parseBlocks()

            if (block.subStack2 != -1) {
                advance()
                secondChildren = parseBlocks()
            }
        }

        return Block(block, params, firstChildren, secondChildren)
    }

    private fun parseParams(block: LogicBlock): List<Any> {
        return block.parameters.map { param ->
            if (param.startsWith("@")) {
                val paramBlockId = param.substring(1, param.length)

                if (!rawBlocks.containsKey(paramBlockId))
                    throw RuntimeException(
                        "This project is possibly corrupted, Trying to find the parameter of a block with the id " +
                        "of ${block.id} but can't find one")

                blacklistedIds.add(paramBlockId)

                return@map parseBlock(rawBlocks[paramBlockId]!!)
            } else {

                return@map param
            }
        }
    }
}