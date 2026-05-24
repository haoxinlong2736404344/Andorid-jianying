# 选题一：剪映订阅挽留弹窗实现说明

## 目标

实现一个可由 DSL 和数据驱动的订阅挽留弹窗，用来展示会员即将流失时的优惠、权益和操作按钮。

## 需求映射

| 选题要求 | 当前实现 |
| --- | --- |
| 弹窗 | `samples/valid/retention_dialog.json` 根节点使用半透明遮罩 `Box` + 白色圆角 `Column` |
| 背景图 | `Image` 节点 `asset://retention_hero.png`，当前用跨平台占位渲染 |
| 会员角标 | Hero 区域中的 `VIP` Badge |
| 标题 | `{ user.name }, keep your Pro access` |
| 打折信息 | `{ campaign.currentPrice }`、`{ campaign.originalPrice }`、`Save { campaign.discount }` |
| 权益小图 | `ForEach` 渲染 `retention.benefits`，每项包含 icon、title、subtitle |
| 按钮 | 主按钮 `Keep Pro` 和次按钮 `Maybe later` |
| 点击事件 | 主按钮 `Track`，次按钮 `Toast` |
| 数据绑定 | 用户名、折扣、价格、剩余天数、权益列表均来自 data |
| 可运行 Demo | Android Demo 默认可切换到 `retention_dialog` |

## 核心文件

- DSL 样例：`samples/valid/retention_dialog.json`
- 内置 Demo 样例：`shared/src/commonMain/kotlin/com/composeflow/camp/dynamic/demo/DemoSamples.kt`
- Demo 数据：`shared/src/commonMain/kotlin/com/composeflow/camp/dynamic/demo/DemoRegistry.kt`
- 渲染器：`shared/src/commonMain/kotlin/com/composeflow/camp/dynamic/render/DynamicRenderer.kt`

## 新增样式能力

为了让弹窗效果更接近预期，本次补充了：

- `fontWeight`：支持 `Normal`、`Medium`、`Bold`
- `textAlign`：支持 `Start`、`Center`、`End`
- `borderColor`
- `borderWidth`

这些字段都经过 Parser 校验，并由 Compose Renderer 映射到实际 UI。

## 演示步骤

1. 打开 Android Studio。
2. 打开 `composeflow-camp`。
3. 等待 Gradle Sync 完成。
4. 运行 `androidApp`。
5. 在 Demo 中选择 `Valid` -> `retention_dialog`。
6. 点击 `Keep Pro`，应看到 Track 事件反馈。
7. 点击 `Maybe later`，应看到 Toast 反馈。
