package com.composeflow.camp.dynamic.demo

object DemoSamples {
    val retentionDialog = """
        {
          "version": "1.0",
          "metadata": {
            "scene": "capcut_subscription_retention_dialog",
            "goal": "retain_vip_user"
          },
          "root": {
            "type": "Box",
            "style": {
              "width": 390,
              "height": 720,
              "backgroundColor": "#66000000",
              "padding": { "horizontal": 18, "vertical": 24 },
              "horizontalAlignment": "Center",
              "verticalAlignment": "Center"
            },
            "children": [
              {
                "type": "Column",
                "style": {
                  "width": 354,
                  "backgroundColor": "#FFFFFFFF",
                  "cornerRadius": 28,
                  "padding": { "all": 18 },
                  "horizontalAlignment": "Center"
                },
                "children": [
                  {
                    "type": "Box",
                    "style": {
                      "width": 318,
                      "height": 132,
                      "backgroundColor": "#FFF3D8",
                      "cornerRadius": 22,
                      "margin": { "bottom": 14 },
                      "horizontalAlignment": "Center",
                      "verticalAlignment": "Center"
                    },
                    "children": [
                      {
                        "type": "Image",
                        "url": "asset://retention_hero.png",
                        "description": "CapCut VIP",
                        "style": {
                          "width": 318,
                          "height": 132,
                          "backgroundColor": "#FFF3D8",
                          "textColor": "#8A5A00",
                          "fontWeight": "Bold",
                          "cornerRadius": 22
                        }
                      },
                      {
                        "type": "Text",
                        "text": "VIP",
                        "style": {
                          "width": 56,
                          "height": 28,
                          "backgroundColor": "#111111",
                          "textColor": "#FFD37A",
                          "fontSize": 14,
                          "fontWeight": "Bold",
                          "textAlign": "Center",
                          "cornerRadius": 14,
                          "padding": { "vertical": 5 }
                        }
                      }
                    ]
                  },
                  {
                    "type": "Text",
                    "text": "{ user.name }，保留你的 Pro 权益",
                    "style": {
                      "width": 318,
                      "textColor": "#111111",
                      "fontSize": 24,
                      "fontWeight": "Bold",
                      "textAlign": "Center",
                      "margin": { "bottom": 8 }
                    }
                  },
                  {
                    "type": "Text",
                    "text": "剩余 { campaign.remainingDays } 天，立即续费可锁定 { campaign.discount } 优惠",
                    "style": {
                      "width": 300,
                      "textColor": "#606060",
                      "fontSize": 14,
                      "textAlign": "Center",
                      "margin": { "bottom": 14 }
                    }
                  },
                  {
                    "type": "Row",
                    "style": {
                      "width": 318,
                      "height": 96,
                      "backgroundColor": "#FFF7EA",
                      "borderColor": "#FFE3A3",
                      "borderWidth": 1,
                      "cornerRadius": 18,
                      "padding": { "horizontal": 12, "vertical": 12 },
                      "margin": { "bottom": 16 },
                      "verticalAlignment": "Center"
                    },
                    "children": [
                      {
                        "type": "Column",
                        "style": { "width": 150 },
                        "children": [
                          {
                            "type": "Text",
                            "text": "限时优惠",
                            "style": { "textColor": "#6B4B00", "fontSize": 13, "fontWeight": "Medium" }
                          },
                          {
                            "type": "Text",
                            "text": "{ campaign.currentPrice }",
                            "style": { "textColor": "#111111", "fontSize": 24, "fontWeight": "Bold" }
                          }
                        ]
                      },
                      {
                        "type": "Column",
                        "style": {
                          "width": 136,
                          "horizontalAlignment": "End"
                        },
                        "children": [
                          {
                            "type": "Text",
                            "text": "原价 { campaign.originalPrice }",
                            "style": { "textColor": "#8A8A8A", "fontSize": 11, "textAlign": "End" }
                          },
                          {
                            "type": "Text",
                            "text": "立省 { campaign.discount }",
                            "style": { "textColor": "#E0442E", "fontSize": 14, "fontWeight": "Bold", "textAlign": "End" }
                          }
                        ]
                      }
                    ]
                  },
                  {
                    "type": "HScroll",
                    "style": {
                      "width": 318,
                      "margin": { "bottom": 16 }
                    },
                    "children": [
                      {
                        "type": "ForEach",
                        "items": "retention.benefits",
                        "itemName": "benefit",
                        "children": [
                          {
                            "type": "Column",
                            "style": {
                              "width": 92,
                              "backgroundColor": "#FFFFFF",
                              "cornerRadius": 14,
                              "padding": { "all": 10 },
                              "margin": { "end": 6 },
                              "horizontalAlignment": "Center"
                            },
                            "children": [
                              {
                                "type": "Image",
                                "url": "asset://benefit_icon.png",
                                "description": "{ benefit.icon }",
                                "style": {
                                  "width": 34,
                                  "height": 34,
                                  "backgroundColor": "#111111",
                                  "textColor": "#FFFFFF",
                                  "fontWeight": "Bold",
                                  "cornerRadius": 17,
                                  "margin": { "bottom": 8 }
                                }
                              },
                              {
                                "type": "Text",
                                "text": "{ benefit.title }",
                                "style": {
                                  "width": 72,
                                  "textColor": "#111111",
                                  "fontSize": 12,
                                  "fontWeight": "Bold",
                                  "textAlign": "Center",
                                  "margin": { "bottom": 2 }
                                }
                              },
                              {
                                "type": "Text",
                                "text": "{ benefit.subtitle }",
                                "style": {
                                  "width": 72,
                                  "textColor": "#8A8A8A",
                                  "fontSize": 10,
                                  "textAlign": "Center"
                                }
                              }
                            ]
                          }
                        ]
                      }
                    ]
                  },
                  {
                    "type": "Button",
                    "text": "立即保留 Pro - { campaign.currentPrice }",
                    "style": {
                      "width": 318,
                      "height": 50,
                      "backgroundColor": "#FF4D4F",
                      "textColor": "#FFFFFF",
                      "cornerRadius": 25,
                      "fontSize": 16,
                      "fontWeight": "Bold",
                      "margin": { "bottom": 10 }
                    },
                    "action": {
                      "type": "Track",
                      "payload": {
                        "event": "retention_confirm_click",
                        "target": "primary_button"
                      }
                    }
                  },
                  {
                    "type": "Button",
                    "text": "暂时不用",
                    "style": {
                      "width": 318,
                      "height": 44,
                      "backgroundColor": "#FFFFFFFF",
                      "textColor": "#666666",
                      "cornerRadius": 22,
                      "fontSize": 14,
                      "borderColor": "#E6E6E6",
                      "borderWidth": 1
                    },
                    "action": {
                      "type": "Toast",
                      "payload": { "message": "已关闭挽留弹窗" }
                    }
                  }
                ]
              }
            ]
          }
        }
    """.trimIndent()

    private val subscriptionPage = """
        {
          "version": "1.0",
          "metadata": { "scene": "subscription_page" },
          "root": {
            "type": "Column",
            "style": { "width": 390, "backgroundColor": "#FFF9F0", "padding": { "all": 16 } },
            "children": [
              {
                "type": "Text",
                "text": "{ campaign.title }",
                "style": { "textColor": "#181818", "fontSize": 26, "fontWeight": "Bold", "margin": { "bottom": 8 } }
              },
              {
                "type": "Text",
                "text": "Unlock premium editing tools.",
                "style": { "textColor": "#666666", "fontSize": 14, "margin": { "bottom": 16 } }
              },
              {
                "type": "Button",
                "text": "Continue",
                "style": {
                  "width": 358,
                  "height": 50,
                  "backgroundColor": "#111111",
                  "textColor": "#FFFFFF",
                  "cornerRadius": 25,
                  "fontSize": 16
                },
                "action": { "type": "Navigate", "payload": { "route": "checkout" } }
              }
            ]
          }
        }
    """.trimIndent()

    private val benefitsEmpty = """
        {
          "version": "1.0",
          "metadata": { "scene": "benefits_empty" },
          "root": {
            "type": "StateLayout",
            "state": "{ benefits.state }",
            "style": {
              "width": 390,
              "height": 420,
              "backgroundColor": "#F5F7FA",
              "padding": { "all": 24 },
              "horizontalAlignment": "Center",
              "verticalAlignment": "Center"
            },
            "empty": [
              { "type": "Text", "text": "No benefits yet", "style": { "textColor": "#111111", "fontSize": 20 } },
              { "type": "Text", "text": "New rewards will appear here.", "style": { "textColor": "#666666", "fontSize": 14 } }
            ],
            "content": [
              { "type": "Text", "text": "Benefits loaded" }
            ]
          }
        }
    """.trimIndent()

    private val benefitsList = """
        {
          "version": "1.0",
          "metadata": { "scene": "benefits_list" },
          "root": {
            "type": "StateLayout",
            "state": "{ benefits.state }",
            "style": {
              "width": 390,
              "backgroundColor": "#F5F7FA",
              "padding": { "all": 16 }
            },
            "loading": [
              { "type": "Text", "text": "Loading benefits..." }
            ],
            "empty": [
              { "type": "Text", "text": "No benefits yet" }
            ],
            "error": [
              { "type": "Text", "text": "Failed to load benefits" }
            ],
            "content": [
              {
                "type": "Text",
                "text": "{ user.name } benefits",
                "style": { "fontSize": 22, "fontWeight": "Bold", "textColor": "#111111", "margin": { "bottom": 12 } }
              },
              {
                "type": "ForEach",
                "items": "benefits.items",
                "itemName": "benefit",
                "children": [
                  {
                    "type": "Row",
                    "style": {
                      "height": 56,
                      "backgroundColor": "#FFFFFFFF",
                      "cornerRadius": 12,
                      "padding": { "horizontal": 14, "vertical": 8 },
                      "margin": { "bottom": 10 },
                      "verticalAlignment": "Center"
                    },
                    "styleWhen": [
                      {
                        "when": "{ benefit.status == 'used' }",
                        "style": { "backgroundColor": "#FFECECEC" }
                      }
                    ],
                    "children": [
                      {
                        "type": "Text",
                        "text": "{ benefit.title }",
                        "style": { "textColor": "#111111", "fontSize": 16, "width": 190 }
                      },
                      {
                        "type": "Text",
                        "text": "{ benefit.tag }",
                        "style": { "textColor": "#2A6FDB", "fontSize": 13 }
                      }
                    ]
                  }
                ]
              }
            ]
          }
        }
    """.trimIndent()

    val validSamples: Map<String, String> = mapOf(
        "retention_dialog" to retentionDialog,
        "subscription_page" to subscriptionPage,
        "benefits_empty" to benefitsEmpty,
        "benefits_list" to benefitsList,
    )

    val invalidSamples: Map<String, String> = mapOf(
        "missing_text" to """
            {
              "version": "1.0",
              "root": { "type": "Text", "style": { "textColor": "#111111" } }
            }
        """.trimIndent(),
        "illegal_color" to """
            {
              "version": "1.0",
              "root": { "type": "Text", "text": "bad color", "style": { "textColor": "red" } }
            }
        """.trimIndent(),
        "unknown_component" to """
            {
              "version": "1.0",
              "root": { "type": "Video", "url": "https://example.com/demo.mp4" }
            }
        """.trimIndent(),
        "foreach_missing_items" to """
            {
              "version": "1.0",
              "root": {
                "type": "ForEach",
                "children": [
                  { "type": "Text", "text": "{ item.title }" }
                ]
              }
            }
        """.trimIndent(),
    )
}
