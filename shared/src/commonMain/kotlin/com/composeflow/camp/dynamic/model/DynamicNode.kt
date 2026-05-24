package com.composeflow.camp.dynamic.model

sealed interface DynamicNode {
    val id: String?
    val visible: String?
    val style: UiStyle?
    val styleWhen: List<ConditionalStyle>

    data class Column(
        override val id: String? = null,
        override val visible: String? = null,
        override val style: UiStyle? = null,
        override val styleWhen: List<ConditionalStyle> = emptyList(),
        val children: List<DynamicNode> = emptyList(),
    ) : DynamicNode

    data class Row(
        override val id: String? = null,
        override val visible: String? = null,
        override val style: UiStyle? = null,
        override val styleWhen: List<ConditionalStyle> = emptyList(),
        val children: List<DynamicNode> = emptyList(),
    ) : DynamicNode

    data class Box(
        override val id: String? = null,
        override val visible: String? = null,
        override val style: UiStyle? = null,
        override val styleWhen: List<ConditionalStyle> = emptyList(),
        val children: List<DynamicNode> = emptyList(),
    ) : DynamicNode

    data class Text(
        override val id: String? = null,
        override val visible: String? = null,
        override val style: UiStyle? = null,
        override val styleWhen: List<ConditionalStyle> = emptyList(),
        val text: String,
    ) : DynamicNode

    data class Image(
        override val id: String? = null,
        override val visible: String? = null,
        override val style: UiStyle? = null,
        override val styleWhen: List<ConditionalStyle> = emptyList(),
        val url: String,
        val description: String? = null,
    ) : DynamicNode

    data class Button(
        override val id: String? = null,
        override val visible: String? = null,
        override val style: UiStyle? = null,
        override val styleWhen: List<ConditionalStyle> = emptyList(),
        val text: String,
        val action: EventSpec? = null,
    ) : DynamicNode

    data class ForEach(
        override val id: String? = null,
        override val visible: String? = null,
        override val style: UiStyle? = null,
        override val styleWhen: List<ConditionalStyle> = emptyList(),
        val items: String,
        val itemName: String = "item",
        val children: List<DynamicNode> = emptyList(),
    ) : DynamicNode

    data class StateLayout(
        override val id: String? = null,
        override val visible: String? = null,
        override val style: UiStyle? = null,
        override val styleWhen: List<ConditionalStyle> = emptyList(),
        val state: String,
        val loading: List<DynamicNode> = emptyList(),
        val empty: List<DynamicNode> = emptyList(),
        val error: List<DynamicNode> = emptyList(),
        val content: List<DynamicNode> = emptyList(),
    ) : DynamicNode
}
