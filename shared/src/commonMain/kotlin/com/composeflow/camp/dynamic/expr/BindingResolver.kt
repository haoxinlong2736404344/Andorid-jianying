package com.composeflow.camp.dynamic.expr

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.put

class BindingResolver {
    private val bindingPattern = Regex("\\{\\s*([^}]+?)\\s*}")

    fun renderTemplate(template: String, data: JsonObject): String {
        return bindingPattern.replace(template) { match ->
            val expression = match.groupValues[1].trim()
            evaluateAsString(expression, data) ?: ""
        }
    }

    fun evaluateAsBoolean(expression: String?, data: JsonObject): Boolean {
        if (expression.isNullOrBlank()) return true
        val trimmed = unwrapExpression(expression)
        return when {
            trimmed == "true" -> true
            trimmed == "false" -> false
            "==" in trimmed -> compare(trimmed, "==", data) { left, right -> left == right }
            "!=" in trimmed -> compare(trimmed, "!=", data) { left, right -> left != right }
            ">=" in trimmed -> compareNumber(trimmed, ">=", data) { left, right -> left >= right }
            "<=" in trimmed -> compareNumber(trimmed, "<=", data) { left, right -> left <= right }
            ">" in trimmed -> compareNumber(trimmed, ">", data) { left, right -> left > right }
            "<" in trimmed -> compareNumber(trimmed, "<", data) { left, right -> left < right }
            else -> resolvePath(trimmed, data)?.jsonPrimitiveOrNull()?.booleanOrNull ?: false
        }
    }

    fun evaluateAsString(expression: String, data: JsonObject): String? {
        val trimmed = unwrapExpression(expression)
        return if ("?:" in trimmed) {
            val parts = trimmed.split("?:", limit = 2)
            evaluateAsString(parts[0].trim(), data).takeUnless { it.isNullOrBlank() }
                ?: parts.getOrNull(1)?.trim()?.trim('\'', '"')
        } else if ("?" in trimmed && ":" in trimmed) {
            val questionIndex = trimmed.indexOf("?")
            val colonIndex = trimmed.indexOf(":", startIndex = questionIndex + 1)
            val condition = trimmed.substring(0, questionIndex)
            val trueValue = trimmed.substring(questionIndex + 1, colonIndex)
            val falseValue = trimmed.substring(colonIndex + 1)
            if (evaluateAsBoolean(condition, data)) {
                evaluateAsString(trueValue.trim(), data)
            } else {
                evaluateAsString(falseValue.trim(), data)
            }
        } else {
            resolvePath(trimmed, data)?.jsonPrimitiveOrNull()?.contentOrNull
                ?: trimmed.trim('\'', '"')
        }
    }

    private fun unwrapExpression(expression: String): String {
        val trimmed = expression.trim()
        return if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            trimmed.removePrefix("{").removeSuffix("}").trim()
        } else {
            trimmed
        }
    }

    fun resolvePath(path: String, data: JsonObject): JsonElement? {
        return path.split(".")
            .filter { it.isNotBlank() }
            .fold(data as JsonElement?) { current, segment ->
                (current as? JsonObject)?.get(segment)
            }
            ?.takeUnless { it is JsonNull }
    }

    fun withScopedValue(data: JsonObject, name: String, value: JsonElement, index: Int): JsonObject {
        return buildJsonObject {
            data.forEach { (key, element) -> put(key, element) }
            put(name, value)
            put("${name}Index", index)
        }
    }

    private fun compare(expression: String, operator: String, data: JsonObject, predicate: (String?, String) -> Boolean): Boolean {
        val (left, right) = splitBinary(expression, operator)
        return predicate(evaluateAsString(left, data), right.trim().trim('\'', '"'))
    }

    private fun compareNumber(expression: String, operator: String, data: JsonObject, predicate: (Double, Double) -> Boolean): Boolean {
        val (left, right) = splitBinary(expression, operator)
        val leftNumber = resolvePath(left.trim(), data)?.jsonPrimitiveOrNull()?.doubleOrNull ?: return false
        val rightNumber = right.trim().toDoubleOrNull() ?: return false
        return predicate(leftNumber, rightNumber)
    }

    private fun splitBinary(expression: String, operator: String): Pair<String, String> {
        val index = expression.indexOf(operator)
        return expression.substring(0, index) to expression.substring(index + operator.length)
    }

    private fun JsonElement.jsonPrimitiveOrNull(): JsonPrimitive? = this as? JsonPrimitive
}
