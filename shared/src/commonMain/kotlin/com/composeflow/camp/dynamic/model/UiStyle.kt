package com.composeflow.camp.dynamic.model

data class UiStyle(
    val width: Float? = null,
    val height: Float? = null,
    val backgroundColor: String? = null,
    val textColor: String? = null,
    val fontSize: Int? = null,
    val fontWeight: FontWeightToken? = null,
    val textAlign: TextAlignToken? = null,
    val padding: EdgeInsets? = null,
    val margin: EdgeInsets? = null,
    val cornerRadius: Float? = null,
    val borderColor: String? = null,
    val borderWidth: Float? = null,
    val horizontalAlignment: HorizontalAlignment? = null,
    val verticalAlignment: VerticalAlignment? = null,
) {
    fun merge(override: UiStyle): UiStyle = UiStyle(
        width = override.width ?: width,
        height = override.height ?: height,
        backgroundColor = override.backgroundColor ?: backgroundColor,
        textColor = override.textColor ?: textColor,
        fontSize = override.fontSize ?: fontSize,
        fontWeight = override.fontWeight ?: fontWeight,
        textAlign = override.textAlign ?: textAlign,
        padding = override.padding ?: padding,
        margin = override.margin ?: margin,
        cornerRadius = override.cornerRadius ?: cornerRadius,
        borderColor = override.borderColor ?: borderColor,
        borderWidth = override.borderWidth ?: borderWidth,
        horizontalAlignment = override.horizontalAlignment ?: horizontalAlignment,
        verticalAlignment = override.verticalAlignment ?: verticalAlignment,
    )
}

data class EdgeInsets(
    val start: Float = 0f,
    val top: Float = 0f,
    val end: Float = 0f,
    val bottom: Float = 0f,
)

enum class HorizontalAlignment {
    Start,
    Center,
    End,
}

enum class VerticalAlignment {
    Top,
    Center,
    Bottom,
}

enum class FontWeightToken {
    Normal,
    Medium,
    Bold,
}

enum class TextAlignToken {
    Start,
    Center,
    End,
}
