package com.iyxan23.asperge.sketchware.models.logic

class EventsLogicSection(
    name: String,
    contextName: String,

    val events: List<Event>
) : BaseLogicSection(name, contextName)