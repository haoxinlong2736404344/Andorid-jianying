package com.composeflow.camp.dynamic.parser

import com.composeflow.camp.dynamic.model.ConditionalStyle
import com.composeflow.camp.dynamic.model.DynamicNode
import com.composeflow.camp.dynamic.model.DynamicPage
import com.composeflow.camp.dynamic.model.EdgeInsets
import com.composeflow.camp.dynamic.model.EventSpec
import com.composeflow.camp.dynamic.model.EventType
import com.composeflow.camp.dynamic.model.FontWeightToken
import com.composeflow.camp.dynamic.model.HorizontalAlignment
import com.composeflow.camp.dynamic.model.TextAlignToken
import com.composeflow.camp.dynamic.model.UiStyle
import com.composeflow.camp.dynamic.model.VerticalAlignment
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.intOrNull

class DynamicDslParser(
    private val json: Json = Json {
        ignoreUnknownKeys = false
        isLenient = true
    },
) {
    fun parse(raw: String): ParseResult<DynamicPage> {
        val errors = mutableListOf<ParseError>()
        val element = runCatching { json.parseToJsonElement(raw) }.getOrElse {
            return ParseResult.Failure(listOf(ParseError("$", "Invalid JSON: ${it.message}")))
        }

        val rootObject = element as? JsonObject
            ?: return ParseResult.Failure(listOf(ParseError("$", "Root must be a JSON object")))

        val version = rootObject.string("version")
        if (version == null) errors += ParseError("$.version", "Required field version is missing")

        val rootElement = rootObject["root"]
        if (rootElement == null) {
            errors += ParseError("$.root", "Required field root is missing")
            return ParseResult.Failure(errors)
        }

        val root = parseNode(rootElement, "$.root", errors)
        val metadata = parseStringMap(rootObject["metadata"], "$.metadata", errors)

        return if (root != null && version != null && errors.isEmpty()) {
            ParseResult.Success(DynamicPage(version = version, root = root, metadata = metadata))
        } else {
            ParseResult.Failure(errors)
        }
    }

    private fun parseNode(element: JsonElement, path: String, errors: MutableList<ParseError>): DynamicNode? {
        val obj = element as? JsonObject ?: run {
            errors += ParseError(path, "Node must be a JSON object")
            return null
        }

        val type = obj.string("type")
        if (type == null) {
            errors += ParseError("$path.type", "Required field type is missing")
            return null
        }

        val id = obj.string("id")
        val visible = obj.string("visible") ?: obj.boolean("visible")?.toString()
        val style = parseStyle(obj["style"], "$path.style", errors)
        val styleWhen = parseConditionalStyles(obj["styleWhen"], "$path.styleWhen", errors)

        return when (type) {
            "Column" -> DynamicNode.Column(
                id = id,
                visible = visible,
                style = style,
                styleWhen = styleWhen,
                children = parseChildren(obj, path, errors),
            )
            "Row" -> DynamicNode.Row(
                id = id,
                visible = visible,
                style = style,
                styleWhen = styleWhen,
                children = parseChildren(obj, path, errors),
            )
            "Box" -> DynamicNode.Box(
                id = id,
                visible = visible,
                style = style,
                styleWhen = styleWhen,
                children = parseChildren(obj, path, errors),
            )
            "Text" -> {
                val text = obj.string("text")
                if (text == null) {
                    errors += ParseError("$path.text", "Text requires text")
                    null
                } else {
                    DynamicNode.Text(id = id, visible = visible, style = style, styleWhen = styleWhen, text = text)
                }
            }
            "Image" -> {
                val url = obj.string("url")
                if (url == null) {
                    errors += ParseError("$path.url", "Image requires url")
                    null
                } else {
                    DynamicNode.Image(
                        id = id,
                        visible = visible,
                        style = style,
                        styleWhen = styleWhen,
                        url = url,
                        description = obj.string("description"),
                    )
                }
            }
            "Button" -> {
                val text = obj.string("text")
                if (text == null) {
                    errors += ParseError("$path.text", "Button requires text")
                    null
                } else {
                    DynamicNode.Button(
                        id = id,
                        visible = visible,
                        style = style,
                        styleWhen = styleWhen,
                        text = text,
                        action = parseEvent(obj["action"], "$path.action", errors),
                    )
                }
            }
            "ForEach" -> {
                val items = obj.string("items")
                if (items == null) {
                    errors += ParseError("$path.items", "ForEach requires items")
                    null
                } else {
                    DynamicNode.ForEach(
                        id = id,
                        visible = visible,
                        style = style,
                        styleWhen = styleWhen,
                        items = items,
                        itemName = obj.string("itemName") ?: "item",
                        children = parseChildren(obj, path, errors),
                    )
                }
            }
            "StateLayout" -> {
                val state = obj.string("state")
                if (state == null) {
                    errors += ParseError("$path.state", "StateLayout requires state")
                    null
                } else {
                    DynamicNode.StateLayout(
                        id = id,
                        visible = visible,
                        style = style,
                        styleWhen = styleWhen,
                        state = state,
                        loading = parseNamedChildren(obj, "loading", path, errors),
                        empty = parseNamedChildren(obj, "empty", path, errors),
                        error = parseNamedChildren(obj, "error", path, errors),
                        content = parseNamedChildren(obj, "content", path, errors),
                    )
                }
            }
            else -> {
                errors += ParseError("$path.type", "Unknown component type: $type")
                null
            }
        }
    }

    private fun parseChildren(obj: JsonObject, path: String, errors: MutableList<ParseError>): List<DynamicNode> {
        return parseNodeArray(obj["children"], "$path.children", errors)
    }

    private fun parseNamedChildren(
        obj: JsonObject,
        name: String,
        path: String,
        errors: MutableList<ParseError>,
    ): List<DynamicNode> = parseNodeArray(obj[name], "$path.$name", errors)

    private fun parseNodeArray(
        element: JsonElement?,
        path: String,
        errors: MutableList<ParseError>,
    ): List<DynamicNode> {
        if (element == null) return emptyList()
        val array = element as? JsonArray ?: run {
            errors += ParseError(path, "Node array expected")
            return emptyList()
        }
        return array.mapIndexedNotNull { index, child -> parseNode(child, "$path[$index]", errors) }
    }

    private fun parseConditionalStyles(
        element: JsonElement?,
        path: String,
        errors: MutableList<ParseError>,
    ): List<ConditionalStyle> {
        if (element == null) return emptyList()
        val array = element as? JsonArray ?: run {
            errors += ParseError(path, "styleWhen must be an array")
            return emptyList()
        }
        return array.mapIndexedNotNull { index, item ->
            val obj = item as? JsonObject ?: run {
                errors += ParseError("$path[$index]", "Conditional style must be an object")
                return@mapIndexedNotNull null
            }
            val whenExpr = obj.string("when")
            if (whenExpr == null) {
                errors += ParseError("$path[$index].when", "Conditional style requires when")
                return@mapIndexedNotNull null
            }
            val style = parseStyle(obj["style"], "$path[$index].style", errors)
            if (style == null) null else ConditionalStyle(whenExpr = whenExpr, style = style)
        }
    }

    private fun parseStyle(element: JsonElement?, path: String, errors: MutableList<ParseError>): UiStyle? {
        if (element == null) return null
        val obj = element as? JsonObject ?: run {
            errors += ParseError(path, "style must be a JSON object")
            return null
        }

        obj.string("backgroundColor")?.let { validateColor(it, "$path.backgroundColor", errors) }
        obj.string("textColor")?.let { validateColor(it, "$path.textColor", errors) }
        obj.string("borderColor")?.let { validateColor(it, "$path.borderColor", errors) }

        return UiStyle(
            width = obj.float("width"),
            height = obj.float("height"),
            backgroundColor = obj.string("backgroundColor"),
            textColor = obj.string("textColor"),
            fontSize = obj.int("fontSize"),
            fontWeight = obj.enumOrNull<FontWeightToken>("fontWeight", "$path.fontWeight", errors),
            textAlign = obj.enumOrNull<TextAlignToken>("textAlign", "$path.textAlign", errors),
            padding = parseInsets(obj["padding"], "$path.padding", errors),
            margin = parseInsets(obj["margin"], "$path.margin", errors),
            cornerRadius = obj.float("cornerRadius"),
            borderColor = obj.string("borderColor"),
            borderWidth = obj.float("borderWidth"),
            horizontalAlignment = obj.enumOrNull<HorizontalAlignment>("horizontalAlignment", "$path.horizontalAlignment", errors),
            verticalAlignment = obj.enumOrNull<VerticalAlignment>("verticalAlignment", "$path.verticalAlignment", errors),
        )
    }

    private fun parseInsets(element: JsonElement?, path: String, errors: MutableList<ParseError>): EdgeInsets? {
        if (element == null) return null
        val obj = element as? JsonObject ?: run {
            errors += ParseError(path, "Insets must be a JSON object")
            return null
        }
        return EdgeInsets(
            start = obj.float("start") ?: obj.float("horizontal") ?: obj.float("all") ?: 0f,
            top = obj.float("top") ?: obj.float("vertical") ?: obj.float("all") ?: 0f,
            end = obj.float("end") ?: obj.float("horizontal") ?: obj.float("all") ?: 0f,
            bottom = obj.float("bottom") ?: obj.float("vertical") ?: obj.float("all") ?: 0f,
        )
    }

    private fun parseEvent(element: JsonElement?, path: String, errors: MutableList<ParseError>): EventSpec? {
        if (element == null) return null
        val obj = element as? JsonObject ?: run {
            errors += ParseError(path, "action must be a JSON object")
            return null
        }
        val rawType = obj.string("type")
        if (rawType == null) {
            errors += ParseError("$path.type", "action requires type")
            return null
        }
        val type = EventType.entries.firstOrNull { it.name == rawType }
        if (type == null) {
            errors += ParseError("$path.type", "Unknown event type: $rawType")
            return null
        }
        return EventSpec(type, parseStringMap(obj["payload"], "$path.payload", errors))
    }

    private fun parseStringMap(element: JsonElement?, path: String, errors: MutableList<ParseError>): Map<String, String> {
        if (element == null) return emptyMap()
        val obj = element as? JsonObject ?: run {
            errors += ParseError(path, "Map must be a JSON object")
            return emptyMap()
        }
        return obj.mapNotNull { (key, value) ->
            val primitive = value as? JsonPrimitive
            val content = primitive?.content
            if (content == null) {
                errors += ParseError("$path.$key", "Map value must be a string")
                null
            } else {
                key to content
            }
        }.toMap()
    }

    private fun validateColor(value: String, path: String, errors: MutableList<ParseError>) {
        val legal = Regex("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{8})$").matches(value)
        if (!legal) errors += ParseError(path, "Color must be #RRGGBB or #AARRGGBB: $value")
    }

    private fun JsonObject.string(key: String): String? = (this[key] as? JsonPrimitive)?.content
    private fun JsonObject.boolean(key: String): Boolean? = (this[key] as? JsonPrimitive)?.booleanOrNull
    private fun JsonObject.float(key: String): Float? = (this[key] as? JsonPrimitive)?.floatOrNull
    private fun JsonObject.int(key: String): Int? = (this[key] as? JsonPrimitive)?.intOrNull

    private inline fun <reified T : Enum<T>> JsonObject.enumOrNull(
        key: String,
        path: String,
        errors: MutableList<ParseError>,
    ): T? {
        val value = string(key) ?: return null
        return enumValues<T>().firstOrNull { it.name == value }.also {
            if (it == null) errors += ParseError(path, "Invalid enum value: $value")
        }
    }
}
