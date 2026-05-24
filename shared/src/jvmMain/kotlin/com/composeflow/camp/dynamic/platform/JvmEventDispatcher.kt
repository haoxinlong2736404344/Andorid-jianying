package com.composeflow.camp.dynamic.platform

import com.composeflow.camp.dynamic.model.EventSpec

actual fun createDefaultEventDispatcher(): EventDispatcher = object : EventDispatcher {
    override fun dispatch(event: EventSpec) {
        println("Dynamic event: $event")
    }
}
