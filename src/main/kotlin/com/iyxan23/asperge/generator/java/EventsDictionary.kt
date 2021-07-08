package com.iyxan23.asperge.generator.java

import com.iyxan23.asperge.sketchware.models.projectfiles.logic.Event

object EventsDictionary {
    fun generateCode(event: Event, blocksCode: String): String {
        return when (event.eventName) {
            "onClick" ->
"""
${event.targetId}.setOnClickListener(new View.OnClickListener() {
$blocksCode
});
""".prependIndent(" ".repeat(8))

            else -> """// Unknown event ${event.eventName}"""
        }
    }
}