package com.composeflow.camp.dynamic.model

data class ConditionalStyle(
    val whenExpr: String,
    val style: UiStyle,
)
