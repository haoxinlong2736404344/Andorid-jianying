# 测试与验证记录

## 样例覆盖

主场景：

- `samples/valid/retention_dialog.json`

合法样例：

- `samples/valid/retention_dialog.json`
- `samples/valid/subscription_page.json`
- `samples/valid/benefits_empty.json`
- `samples/valid/benefits_list.json`

错误样例：

- `samples/invalid/missing_text.json`
- `samples/invalid/illegal_color.json`
- `samples/invalid/unknown_component.json`
- `samples/invalid/foreach_missing_items.json`

## 本地样例校验

执行：

```bash
python tools/validate_samples.py
```

预期：

```text
OK   samples/valid/benefits_empty.json
OK   samples/valid/benefits_list.json
OK   samples/valid/retention_dialog.json
OK   samples/valid/subscription_page.json
OK   samples/invalid/foreach_missing_items.json
OK   samples/invalid/illegal_color.json
OK   samples/invalid/missing_text.json
OK   samples/invalid/unknown_component.json
```

这里的 `invalid` 也显示 OK，含义是“它确实被识别为错误配置”。

## Kotlin 单测

默认使用仓库内 Gradle Wrapper：

```bash
bash ./gradlew :shared:jvmTest
```

覆盖点：

- 合法 Text 页面能解析为 `ParseResult.Success`。
- 非法颜色能返回 `ParseResult.Failure`。
- `ForEach` 和 `StateLayout` 能解析成功。
- `{ user.name }` 数据绑定能替换。
- 布尔、比较和三元表达式能计算。

## Android Demo 验证

如果本机 Android 环境可用：

```bash
bash ./gradlew :androidApp:installDebug
```

人工检查：

1. 打开 Demo，切换 `Valid` 下的 4 个样例。
2. 确认订阅挽留弹窗、订阅页、权益空态、权益列表都能展示。
3. 点击动态按钮，确认 Toast 展示 Track / Navigate / Toast 占位反馈。
4. 切换 `Invalid`，确认错误面板展示具体 path 和 message。
5. 查看 `benefits_list`，确认 `ForEach` 渲染多条权益，`styleWhen` 对已领取项生效。

## 选题一专项验证点

- `retention_dialog` 解析成功，且关键节点包含 `VIP` 角标、价格与折扣绑定文案。
- 主按钮事件为 `Track`，事件名为 `retention_confirm_click`。
- 次按钮事件为 `Toast`，文案为 `Dialog dismissed`。
- 缺失绑定路径不再回退字段名字面量，而是渲染为空字符串。

## 剩余风险

- 当前环境可能受网络/插件镜像影响，Gradle 依赖解析存在失败风险。
- iOS 端仍为图片占位渲染，Android 端已支持最小可用真实图片渲染（`asset://` 与网络 URL）。
- 远端配置拉取、签名校验和灰度策略属于生产化扩展，本次以训练营闭环为主。
