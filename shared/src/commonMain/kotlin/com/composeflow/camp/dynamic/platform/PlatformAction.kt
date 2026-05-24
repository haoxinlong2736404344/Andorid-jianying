package com.composeflow.camp.dynamic.platform

import com.composeflow.camp.dynamic.model.EventSpec

interface EventDispatcher {
    fun dispatch(event: EventSpec)
}

expect fun createDefaultEventDispatcher(): EventDispatcher
