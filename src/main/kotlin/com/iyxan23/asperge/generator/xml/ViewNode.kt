package com.iyxan23.asperge.generator.xml

import com.iyxan23.asperge.sketchware.models.projectfiles.view.ViewItem

data class ViewNode(
    val view: ViewItem,
    val childs: ArrayList<ViewNode>,
)