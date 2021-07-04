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

    val viewIdTypes = ArrayList<Pair<List<String>, List<String>>>()

    parsed.view.sections.forEach {
        val generator = XmlLayoutGenerator(it, parsed.resource, parsed.file, parsed.project)

        println(generator.generate())

        viewIdTypes.add(Pair(generator.getViewIDs(), generator.getViewTypes()))
    }

    activities.keys.forEachIndexed { index, key ->
        println(
            JavaGenerator(
                activities[key] as List<BaseLogicSection>,
                viewIdTypes[index].first,
                viewIdTypes[index].second,
                parsed.project
            ).generate()
        )
    }
}