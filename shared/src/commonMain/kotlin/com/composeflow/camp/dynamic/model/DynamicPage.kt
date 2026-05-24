package com.composeflow.camp.dynamic.model

data class DynamicPage(
    val version: String,
    val root: DynamicNode,
    val metadata: Map<String, String> = emptyMap(),
)
