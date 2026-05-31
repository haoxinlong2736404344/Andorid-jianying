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
                "title": "会员专属创作包",
                "discount": "50%",
                "originalPrice": "19.99 元",
                "currentPrice": "9.99 元",
                "remainingDays": 2
              },
              "retention": {
                "benefits": [
                  { "icon": "4K", "title": "高清导出", "subtitle": "无水印" },
                  { "icon": "BGM", "title": "音乐素材", "subtitle": "可商用" },
                  { "icon": "AI", "title": "AI 工具", "subtitle": "智能剪辑" }
                ]
              }
            }
        """.trimIndent()
        "subscription_page" -> """
            {
              "user": { "name": "Andy", "isVip": false },
              "campaign": { "title": "夏日创作季", "discount": "40%" },
              "paywall": { "selectedPlan": "yearly" }
            }
        """.trimIndent()
        "benefits_list" -> """
            {
              "user": { "name": "Andy", "isVip": true },
              "benefits": {
                "state": "content",
                "items": [
                  { "title": "商用素材包", "status": "available", "tag": "新权益" },
                  { "title": "高清导出额度", "status": "used", "tag": "已领取" },
                  { "title": "云空间扩容", "status": "available", "tag": "限时" }
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
