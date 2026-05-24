# 设计说明

## 业务场景

本方案选择“订阅商业化页面”作为动态化场景，覆盖订阅挽留弹窗、订阅页、权益空态和权益列表。这个场景适合动态化，因为运营经常调整标题、折扣、权益文案、模块顺序、按钮行为和展示条件，但不一定希望每次都等待端发版。

## 动态点

- 页面结构：运营位、权益卡片、按钮等模块由 DSL 描述顺序和层级。
- 文案：通过 `{ user.name }`、`{ campaign.discount }` 等表达式绑定数据。
- 样式：颜色、字号、间距、圆角、背景和对齐可配置。
- 条件样式：通过 `styleWhen` 按数据状态覆盖局部样式。
- 状态：通过 `StateLayout` 支持 loading、empty、error、content。
- 列表：通过 `ForEach` 绑定数组数据并重复渲染子树。
- 行为：通过统一事件模型分发 Toast、跳转和埋点。

## 架构

```text
JSON DSL
   |
   v
DynamicDslParser
   |
   v
DynamicPage / DynamicNode / UiStyle
   |
   v
DynamicPageRenderer
   |
   v
Compose Multiplatform UI
```

## 为什么要有 IR

Renderer 不直接消费原始 JSON，而是消费 `DynamicPage` 这类类型安全模型。好处是：

- Parser 阶段集中处理错误，不让坏配置直接进入 UI。
- Renderer 只处理受控组件集合，逻辑更稳定。
- 后续可以让不同 DSL 版本统一转换到同一个 IR。
- 测试可以覆盖 Parser 和 Renderer 映射，不必在每个页面重复判断字段。

## 错误处理策略

Parser 会收集所有能发现的错误，而不是遇到第一个错误就崩溃：

- 缺少 `version` / `root` / `type`。
- `Text` 缺少 `text`。
- `Image` 缺少 `url`。
- `ForEach` 缺少 `items`。
- `StateLayout` 缺少 `state`。
- 未知组件类型。
- 非法颜色。
- 非法事件类型。
- 非法枚举值。

调用方拿到 `ParseResult.Failure(errors)` 后可以展示错误面板、回退兜底页或上报配置平台。

## expect/actual 边界

shared 层不直接调用平台 API。事件通过 `EventDispatcher` 分发，默认提供 JVM 和 Android 实现。Android Demo 中点击动态按钮会进入 `AndroidUiEventDispatcher`，再映射到 Toast、Navigate 和 Track 的占位行为。

后续 iOS 可以补充：

- Toast / Alert 实现。
- 页面跳转实现。
- 埋点 SDK 实现。
- 图片加载或资源读取实现。

## 取舍

本版本优先保证训练营闭环完整，不追求大而全：

- 图片能力通过 `PlatformDynamicImage` expect/actual 隔离：Android 端支持 `asset://` 与网络 URL，其他平台先占位，避免把平台图片库选择绑定到核心 DSL。
- 表达式支持变量、布尔、比较和简单三元表达式，不支持任意脚本。
- 组件集合保持克制，覆盖文档要求的基础组件和 P1 关键扩展。
- 远端配置平台、灰度发布、签名校验属于生产化能力，本次以接口和文档说明为主。

## 后续生产化扩展

1. 增加远端 DSL 拉取、缓存、签名校验和灰度策略。
2. 增加平台图片组件 actual 实现。
3. 增加埋点白名单和跳转路由白名单。
4. 增加可视化 DSL 编辑器或配置平台预览能力。
5. 增加更完整的截图测试和端侧回归测试。
