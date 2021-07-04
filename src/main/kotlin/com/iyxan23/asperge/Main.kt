package com.iyxan23.asperge

import com.iyxan23.asperge.generator.java.JavaGenerator
import com.iyxan23.asperge.generator.xml.XmlLayoutGenerator
import com.iyxan23.asperge.sketchware.models.projectfiles.logic.BaseLogicSection
import com.iyxan23.asperge.unpacker.Unpacker

fun main(args: Array<String>) {
    val unpacked = Unpacker.unpack(args[0])
    val parsed = unpacked.parse()

    val activities = HashMap<String, ArrayList<BaseLogicSection>>()

    parsed.logic.sections.forEach { section ->
        if (!activities.containsKey(section.name)) {
            activities[section.name] = ArrayList()
        }

        activities[section.name]!!.add(section)
    }

    parsed.view.sections.forEach {
        println(XmlLayoutGenerator(it, parsed.resource, parsed.file, parsed.project).generate())
    }

    activities.keys.forEach { key ->
        println(JavaGenerator(activities[key] as List<BaseLogicSection>, parsed.project).generate())
    }
}