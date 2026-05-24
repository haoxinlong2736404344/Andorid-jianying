package com.composeflow.camp.dynamic.parser

import com.composeflow.camp.dynamic.demo.DemoSamples
import com.composeflow.camp.dynamic.model.DynamicNode
import com.composeflow.camp.dynamic.model.EventType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
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

    @Test
    fun parseRetentionDialogAndVerifyActions() {
        val raw = DemoSamples.validSamples.getValue("retention_dialog")
        val result = parser.parse(raw)
        assertTrue(result is ParseResult.Success)

        val rootChildren = (result.value.root as DynamicNode.Box).children
        val dialogColumn = rootChildren.first() as DynamicNode.Column
        val nodes = dialogColumn.children
        val title = nodes[1] as DynamicNode.Text
        val primary = nodes[5] as DynamicNode.Button
        val secondary = nodes[6] as DynamicNode.Button

        assertTrue(title.text.contains("{ user.name }"))
        assertTrue(primary.text.contains("{ campaign.currentPrice }"))
        assertEquals(EventType.Track, primary.action?.type)
        assertEquals("retention_confirm_click", primary.action?.payload?.get("event"))
        assertEquals(EventType.Toast, secondary.action?.type)
        assertEquals("Dialog dismissed", secondary.action?.payload?.get("message"))
        assertNotNull(primary.action)
        assertNotNull(secondary.action)
    }
}
