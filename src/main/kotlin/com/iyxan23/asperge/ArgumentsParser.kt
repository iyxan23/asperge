package com.iyxan23.asperge

class ArgumentsParser(
    private val flags: List<String> = emptyList(),
    private val strings: List<String> = emptyList(),
) {
    private var captureNext = false
    private var lastStringFlag = ""

    //                                       flag    string value
    fun parse(args: List<String>): List<Pair<String, String?>> {
        return args.mapNotNull {
            when {
                captureNext -> {
                    captureNext = false
                    Pair(it, lastStringFlag)
                }

                flags.contains(it) -> Pair(it, null)

                strings.contains(it) -> {
                    captureNext = true
                    lastStringFlag = it
                    Pair(it, null)
                }

                else -> null
            }
        }
    }
}