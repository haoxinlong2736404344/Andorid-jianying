# 训练营提交检查清单

## P0 基础闭环能力

| 文档要求 | 本工程对应产物 |
| --- | --- |
| 场景选择与 DSL 设计 | `docs/DESIGN.md`、`docs/DSL_SPEC.md` |
| 支持页面树结构 | `DynamicNode.Column` / `Row` / `Box` 与 `children` |
| 基础组件 | `Column`、`Row`、`Box`、`Text`、`Image`、`Button` |
| 样式能力 | `UiStyle` 支持宽高、颜色、字号、间距、圆角、对齐 |
| 配置解析与校验 | `DynamicDslParser`、`ParseError`、`ParseResult` |
| 错误处理演示 | `samples/invalid/*.json`、Android Demo 错误面板 |
| Compose 渲染映射 | `DynamicPageRenderer`、`DynamicNodeRenderer` |
| 数据绑定 | `BindingResolver.renderTemplate()` |
| 基础事件 | `EventSpec`、`EventType`、`EventDispatcher` |
| 3 个合法样例 | 已提供 4 个合法样例 |
| 2 个错误样例 | 已提供 4 个错误样例 |
| README | `README.md` |

## P0 可展示结果能力

| 文档要求 | 本工程状态 |
| --- | --- |
| 至少一端可运行 Demo | `androidApp` Android Demo 模块 |
| 支持样例切换 | `DemoSamples.validSamples` / `DemoSamples.invalidSamples` |
| 支持错误配置场景演示 | Android Demo 的 `Invalid` 模式 |
| DSL 说明 | `docs/DSL_SPEC.md` |
| 设计说明 | `docs/DESIGN.md` |
| 测试/验证记录 | `docs/VALIDATION.md` |

## P1 进阶能力

| 文档要求 | 本工程状态 |
| --- | --- |
| 变量读取 | 已支持，例如 `{ user.name }` |
| 布尔判断 | 已支持，例如 `{ user.isVip }` |
| 比较运算 | 已支持，例如 `{ benefits.count == 0 }` |
| 简单三元表达式 | 已支持，例如 `{ user.isVip ? 'VIP' : 'Guest' }` |
| 列表数据绑定与动态渲染 | 已支持 `ForEach` |
| 空态、加载态、错误态展示 | 已支持 `StateLayout` |
| 条件样式 | 已支持 `styleWhen` |
| 动态更新 | Android Demo 通过切换 DSL 和数据触发重组 |

## 推荐答辩重点

1. Renderer 不直接消费 JSON，而是先转换为类型安全 IR。
2. 配置错误在 Parser 阶段被收集，避免坏配置直接造成 UI 崩溃。
3. 平台能力通过 `EventDispatcher` 隔离，shared 层不直接依赖 Android/iOS API。
4. DSL 能覆盖商业化页面里最常变的结构、文案、样式、状态、列表和事件。
5. 当前边界是有意收敛的：先保证可解释、可验证，再扩展复杂组件和远端能力。
