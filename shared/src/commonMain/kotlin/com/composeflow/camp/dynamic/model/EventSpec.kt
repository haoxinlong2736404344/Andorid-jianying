package com.composeflow.camp.dynamic.model

data class EventSpec(
    val type: EventType,
    val payload: Map<String, String> = emptyMap(),
)

enum class EventType {
    Toast,
    Navigate,
    Track,
}
