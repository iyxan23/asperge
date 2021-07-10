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

    private fun parseBlocks(end: Int = Int.MAX_VALUE): List<Block> {
        val result = ArrayList<Block>()

        while (currentBlock != null) {
            result.add(parseBlock(currentBlock!!))

            if (currentBlock!!.id.toInt() == end) break

            advance()
        }

        return result
    }

    private fun parseBlock(block: LogicBlock): Block {
        var firstChildren: List<Block>? = null
        var secondChildren: List<Block>? = null
        val params = parseParams(block)

        if (block.subStack1 != -1) {
            firstChildren = parseBlocks(block.subStack1)

            if (block.subStack2 != -1) {
                advance()
                secondChildren = parseBlocks(block.subStack2)
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
                advance()

                return@map parseBlock(rawBlocks[paramBlockId]!!)
            } else {

                return@map param
            }
        }
    }
}