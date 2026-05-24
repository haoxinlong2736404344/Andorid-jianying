package com.composeflow.camp.dynamic.demo

object DemoRegistry {
    val demoNames = listOf(
        "retention_dialog",
        "subscription_page",
        "benefits_empty",
        "benefits_list",
    )

    fun sampleData(name: String): String = when (name) {
        "retention_dialog" -> """
            {
              "user": { "name": "Andy", "isVip": true },
              "campaign": {
                "title": "VIP creator pack",
                "discount": "50%",
                "originalPrice": "19.99 USD",
                "currentPrice": "9.99 USD",
                "remainingDays": 2
              },
              "retention": {
                "benefits": [
                  { "icon": "4K", "title": "HD export", "subtitle": "No watermark" },
                  { "icon": "BGM", "title": "Music pack", "subtitle": "Commercial use" },
                  { "icon": "AI", "title": "AI tools", "subtitle": "Smart editing" }
                ]
              }
            }
        """.trimIndent()
        "subscription_page" -> """
            {
              "user": { "name": "Andy", "isVip": false },
              "campaign": { "title": "Summer creator sale", "discount": "40%" },
              "paywall": { "selectedPlan": "yearly" }
            }
        """.trimIndent()
        "benefits_list" -> """
            {
              "user": { "name": "Andy", "isVip": true },
              "benefits": {
                "state": "content",
                "items": [
                  { "title": "Commercial asset pack", "status": "available", "tag": "New" },
                  { "title": "HD export quota", "status": "used", "tag": "Used" },
                  { "title": "Cloud storage boost", "status": "available", "tag": "Limited" }
                ]
              }
            }
        """.trimIndent()
        else -> """
            {
              "user": { "name": "Guest", "isVip": false },
              "benefits": { "count": 0, "state": "empty", "items": [] }
            }
        """.trimIndent()
    }
}
