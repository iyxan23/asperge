package com.iyxan23.asperge.generator.xml

import com.iyxan23.asperge.sketchware.models.projectfiles.view.ViewSection

// This class parses a list of views and turn it into a tree
class ViewsParser(
    private val section: ViewSection
) {
    fun parse(): ArrayList<ViewNode> {
        val map = HashMap<String, Int>()
        val roots = ArrayList<ViewNode>()

        val list = ArrayList<ViewNode>().apply {
            for (view in section.views) {
                add(ViewNode(view, ArrayList()))
            }
        }

        list.forEachIndexed { index, node ->
            map[node.view.id] = index
        }

        list.forEach { node ->
            if (node.view.parent != "root" && map[node.view.parent] != null) {
                list[map[node.view.parent]!!].childs.add(node)
            } else {
                roots.add(node)
            }
        }

        return roots
    }
}