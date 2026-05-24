# 测试与验证记录

## 样例覆盖

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

如果本机 Gradle 环境可用：

```bash
gradle :shared:jvmTest
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
gradle :androidApp:installDebug
```

人工检查：

1. 打开 Demo，切换 `Valid` 下的 4 个样例。
2. 确认订阅挽留弹窗、订阅页、权益空态、权益列表都能展示。
3. 点击动态按钮，确认 Toast 展示 Track / Navigate / Toast 占位反馈。
4. 切换 `Invalid`，确认错误面板展示具体 path 和 message。
5. 查看 `benefits_list`，确认 `ForEach` 渲染多条权益，`styleWhen` 对已领取项生效。

## 剩余风险

- 当前工作区没有 Gradle Wrapper，离线环境无法实际下载 Gradle/Android 依赖。
- 图片加载仍是跨平台占位，真实业务接入时需要补 Android/iOS 图片加载实现。
- 远端配置拉取、签名校验和灰度策略属于生产化扩展，本次以训练营闭环为主。
