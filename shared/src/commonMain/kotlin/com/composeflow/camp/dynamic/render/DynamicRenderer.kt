package com.composeflow.camp.dynamic.render

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composeflow.camp.dynamic.expr.BindingResolver
import com.composeflow.camp.dynamic.model.DynamicNode
import com.composeflow.camp.dynamic.model.DynamicPage
import com.composeflow.camp.dynamic.model.EdgeInsets
import com.composeflow.camp.dynamic.model.EventSpec
import com.composeflow.camp.dynamic.model.FontWeightToken
import com.composeflow.camp.dynamic.model.HorizontalAlignment
import com.composeflow.camp.dynamic.model.TextAlignToken
import com.composeflow.camp.dynamic.model.UiStyle
import com.composeflow.camp.dynamic.model.VerticalAlignment
import com.composeflow.camp.dynamic.platform.EventDispatcher
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

@Composable
fun DynamicPageRenderer(
    page: DynamicPage,
    data: JsonObject,
    eventDispatcher: EventDispatcher,
    modifier: Modifier = Modifier,
) {
    val resolver = remember { BindingResolver() }
    DynamicNodeRenderer(
        node = page.root,
        data = data,
        eventDispatcher = eventDispatcher,
        resolver = resolver,
        modifier = modifier,
    )
}

@Composable
fun DynamicNodeRenderer(
    node: DynamicNode,
    data: JsonObject,
    eventDispatcher: EventDispatcher,
    resolver: BindingResolver,
    modifier: Modifier = Modifier,
) {
    if (!resolver.evaluateAsBoolean(node.visible, data)) return
    val style = node.effectiveStyle(resolver, data)

    when (node) {
        is DynamicNode.Column -> Column(
            modifier = modifier.applyStyle(style),
            horizontalAlignment = style.toComposeHorizontalAlignment(),
            verticalArrangement = style.toComposeVerticalArrangement(),
        ) {
            node.children.forEach {
                DynamicNodeRenderer(it, data, eventDispatcher, resolver)
            }
        }

        is DynamicNode.Row -> Row(
            modifier = modifier.applyStyle(style),
            verticalAlignment = style.toComposeVerticalAlignment(),
            horizontalArrangement = style.toComposeHorizontalArrangement(),
        ) {
            node.children.forEach {
                DynamicNodeRenderer(it, data, eventDispatcher, resolver)
            }
        }

        is DynamicNode.Box -> Box(
            modifier = modifier.applyStyle(style),
            contentAlignment = style.toBoxAlignment(),
        ) {
            node.children.forEach {
                DynamicNodeRenderer(it, data, eventDispatcher, resolver)
            }
        }

        is DynamicNode.HScroll -> {
            // Note: we render children as individual items. For most use-cases, HScroll will contain
            // a single ForEach which will expand into multiple nodes via DynamicNodeRenderer.
            LazyRow(
                modifier = modifier.applyStyle(style),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                items(node.children) { child ->
                    DynamicNodeRenderer(child, data, eventDispatcher, resolver)
                }
            }
        }

//        is DynamicNode.Text -> Text(
//            text = resolver.renderTemplate(node.text, data),
//            modifier = modifier.applyStyle(style),
//            color = style?.textColor?.toComposeColor() ?: Color.Unspecified,
//            fontSize = style?.fontSize?.sp ?: androidx.compose.ui.unit.TextUnit.Unspecified,
//            fontWeight = style.toComposeFontWeight(),
//            textAlign = style.toComposeTextAlign(),
//        )

        is DynamicNode.Text -> {
            val raw = node.text
            val rendered = resolver.renderTemplate(raw, data)
            android.util.Log.d("DynamicText", "raw=$raw -> rendered=$rendered")

            Text(
                text = rendered,
                modifier = modifier.applyStyle(style),
                color = style?.textColor?.toComposeColor() ?: Color.Unspecified,
                fontSize = style?.fontSize?.sp ?: androidx.compose.ui.unit.TextUnit.Unspecified,
                fontWeight = style.toComposeFontWeight(),
                textAlign = style.toComposeTextAlign(),
            )
        }

        is DynamicNode.Image -> DynamicImagePlaceholder(
            url = resolver.renderTemplate(node.url, data),
            description = node.description?.let { resolver.renderTemplate(it, data) },
            style = style,
            modifier = modifier,
        )

        is DynamicNode.Button -> DynamicButton(
            text = resolver.renderTemplate(node.text, data),
            action = node.action,
            eventDispatcher = eventDispatcher,
            style = style,
            modifier = modifier,
        )

        is DynamicNode.ForEach -> {
            val items = resolver.resolvePath(node.items.toPathExpression(), data) as? JsonArray
            items?.forEachIndexed { index, item ->
                val scopedData = resolver.withScopedValue(data, node.itemName, item, index)
                node.children.forEach {
                    DynamicNodeRenderer(it, scopedData, eventDispatcher, resolver)
                }
            }
        }

        is DynamicNode.StateLayout -> {
            val state = resolver.evaluateAsString(node.state, data)
            val children = when (state) {
                "loading" -> node.loading
                "empty" -> node.empty
                "error" -> node.error
                else -> node.content
            }
            Column(modifier = modifier.applyStyle(style)) {
                children.forEach {
                    DynamicNodeRenderer(it, data, eventDispatcher, resolver)
                }
            }
        }
    }
}

@Composable
private fun DynamicButton(
    text: String,
    action: EventSpec?,
    eventDispatcher: EventDispatcher,
    style: UiStyle?,
    modifier: Modifier,
) {
    Box(
        modifier = modifier
            .applyStyle(style)
            .clickable(enabled = action != null) {
                action?.let(eventDispatcher::dispatch)
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = style?.textColor?.toComposeColor() ?: Color.White,
            fontSize = style?.fontSize?.sp ?: 16.sp,
            fontWeight = style.toComposeFontWeight() ?: FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun DynamicImagePlaceholder(
    url: String,
    description: String?,
    style: UiStyle?,
    modifier: Modifier,
) {
    Box(
        modifier = modifier.applyStyle(style),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = description ?: url,
            fontSize = 12.sp,
            color = style?.textColor?.toComposeColor() ?: Color(0xFF666666),
            fontWeight = style.toComposeFontWeight(),
            textAlign = TextAlign.Center,
        )
    }
}

private fun Modifier.applyStyle(style: UiStyle?): Modifier {
    if (style == null) return this
    var result = this
    style.margin?.let { result = result.padding(it.toPaddingValues()) }
    style.width?.let { result = result.width(it.dp) }
    style.height?.let { result = result.height(it.dp) }
    val shape = RoundedCornerShape((style.cornerRadius ?: 0f).dp)
    style.cornerRadius?.let { result = result.clip(shape) }
    style.backgroundColor?.toComposeColor()?.let { result = result.background(it) }
    val borderColor = style.borderColor?.toComposeColor()
    val borderWidth = style.borderWidth
    if (borderColor != null && borderWidth != null && borderWidth > 0f) {
        result = result.border(borderWidth.dp, borderColor, shape)
    }
    style.padding?.let { result = result.padding(it.toPaddingValues()) }
    return result
}

private fun EdgeInsets.toPaddingValues() = androidx.compose.foundation.layout.PaddingValues(
    start = start.dp,
    top = top.dp,
    end = end.dp,
    bottom = bottom.dp,
)

private fun UiStyle?.toComposeHorizontalAlignment(): Alignment.Horizontal = when (this?.horizontalAlignment) {
    HorizontalAlignment.Center -> Alignment.CenterHorizontally
    HorizontalAlignment.End -> Alignment.End
    else -> Alignment.Start
}

private fun UiStyle?.toComposeVerticalAlignment(): Alignment.Vertical = when (this?.verticalAlignment) {
    VerticalAlignment.Center -> Alignment.CenterVertically
    VerticalAlignment.Bottom -> Alignment.Bottom
    else -> Alignment.Top
}

private fun UiStyle?.toComposeHorizontalArrangement(): Arrangement.Horizontal = when (this?.horizontalAlignment) {
    HorizontalAlignment.Center -> Arrangement.Center
    HorizontalAlignment.End -> Arrangement.End
    else -> Arrangement.Start
}

private fun UiStyle?.toComposeVerticalArrangement(): Arrangement.Vertical = when (this?.verticalAlignment) {
    VerticalAlignment.Center -> Arrangement.Center
    VerticalAlignment.Bottom -> Arrangement.Bottom
    else -> Arrangement.Top
}

private fun UiStyle?.toBoxAlignment(): Alignment = when (this?.horizontalAlignment to this?.verticalAlignment) {
    HorizontalAlignment.Center to VerticalAlignment.Center -> Alignment.Center
    HorizontalAlignment.End to VerticalAlignment.Bottom -> Alignment.BottomEnd
    HorizontalAlignment.Center to VerticalAlignment.Bottom -> Alignment.BottomCenter
    HorizontalAlignment.Start to VerticalAlignment.Bottom -> Alignment.BottomStart
    HorizontalAlignment.End to VerticalAlignment.Center -> Alignment.CenterEnd
    HorizontalAlignment.Start to VerticalAlignment.Center -> Alignment.CenterStart
    HorizontalAlignment.End to VerticalAlignment.Top -> Alignment.TopEnd
    HorizontalAlignment.Center to VerticalAlignment.Top -> Alignment.TopCenter
    else -> Alignment.TopStart
}

private fun UiStyle?.toComposeFontWeight(): FontWeight? = when (this?.fontWeight) {
    FontWeightToken.Medium -> FontWeight.Medium
    FontWeightToken.Bold -> FontWeight.Bold
    FontWeightToken.Normal -> FontWeight.Normal
    null -> null
}

private fun UiStyle?.toComposeTextAlign(): TextAlign? = when (this?.textAlign) {
    TextAlignToken.Center -> TextAlign.Center
    TextAlignToken.End -> TextAlign.End
    TextAlignToken.Start -> TextAlign.Start
    null -> null
}

private fun String.toComposeColor(): Color? {
    val normalized = removePrefix("#")
    return runCatching {
        val argb = when (normalized.length) {
            6 -> 0xFF000000L or normalized.toLong(16)
            8 -> normalized.toLong(16)
            else -> null
        }
        argb?.let { Color(it) }
    }.getOrNull()
}

private fun DynamicNode.effectiveStyle(resolver: BindingResolver, data: JsonObject): UiStyle? {
    return styleWhen.fold(style) { current, conditional ->
        if (resolver.evaluateAsBoolean(conditional.whenExpr, data)) {
            current?.merge(conditional.style) ?: conditional.style
        } else {
            current
        }
    }
}

private fun String.toPathExpression(): String {
    val trimmed = trim()
    return if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
        trimmed.removePrefix("{").removeSuffix("}").trim()
    } else {
        trimmed
    }
}
