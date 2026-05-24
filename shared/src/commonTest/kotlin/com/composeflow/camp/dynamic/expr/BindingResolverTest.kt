package com.composeflow.camp.dynamic.expr

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BindingResolverTest {
    private val resolver = BindingResolver()
    private val data = Json.parseToJsonElement(
        """
        {
          "user": { "name": "Andy", "isVip": true },
          "benefits": { "count": 0 },
          "plan": { "type": "yearly" }
        }
        """.trimIndent(),
    ).jsonObject

    @Test
    fun renderPathBinding() {
        assertEquals("Hello Andy", resolver.renderTemplate("Hello { user.name }", data))
    }

    @Test
    fun evaluateBooleanAndCompareExpressions() {
        assertTrue(resolver.evaluateAsBoolean("{ user.isVip }", data))
        assertTrue(resolver.evaluateAsBoolean("{ benefits.count == 0 }", data))
        assertFalse(resolver.evaluateAsBoolean("{ plan.type == 'monthly' }", data))
    }

    @Test
    fun evaluateTernaryExpression() {
        assertEquals("VIP", resolver.evaluateAsString("{ user.isVip ? 'VIP' : 'Guest' }", data))
    }
}
