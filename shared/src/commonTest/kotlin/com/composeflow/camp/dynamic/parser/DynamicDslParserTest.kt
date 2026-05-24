package com.composeflow.camp.dynamic.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DynamicDslParserTest {
    private val parser = DynamicDslParser()

    @Test
    fun parseValidTextPage() {
        val result = parser.parse(
            """
            {
              "version": "1.0",
              "root": {
                "type": "Column",
                "children": [
                  { "type": "Text", "text": "Hello { user.name }" }
                ]
              }
            }
            """.trimIndent(),
        )

        assertTrue(result is ParseResult.Success)
        assertEquals("1.0", result.value.version)
    }

    @Test
    fun reportIllegalColor() {
        val result = parser.parse(
            """
            {
              "version": "1.0",
              "root": {
                "type": "Text",
                "text": "Hello",
                "style": { "textColor": "red" }
              }
            }
            """.trimIndent(),
        )

        assertTrue(result is ParseResult.Failure)
        assertTrue(result.errors.any { it.path.endsWith("textColor") })
    }

    @Test
    fun parseForEachAndStateLayout() {
        val result = parser.parse(
            """
            {
              "version": "1.0",
              "root": {
                "type": "StateLayout",
                "state": "{ benefits.state }",
                "content": [
                  {
                    "type": "ForEach",
                    "items": "benefits.items",
                    "itemName": "benefit",
                    "children": [
                      { "type": "Text", "text": "{ benefit.title }" }
                    ]
                  }
                ]
              }
            }
            """.trimIndent(),
        )

        assertTrue(result is ParseResult.Success)
    }
}
