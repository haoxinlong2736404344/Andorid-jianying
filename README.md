# ComposeFlow Camp

基于《商业化客户端训练营：给 KMP 增加动态化能力》生成的 KMP/Compose 动态化 UI 方案。

主交付场景：**选题一「剪映订阅挽留弹窗」**（`retention_dialog`）。  
其余样例（`subscription_page`、`benefits_empty`、`benefits_list`）作为能力扩展验证。

## 已完成的训练营目标

1. 用 JSON DSL 描述页面树。
2. 将 DSL 解析并校验为类型安全的中间模型 IR。
3. 用 Compose Multiplatform Renderer 把 IR 映射为 Composable UI。
4. 支持基础数据绑定、可见性表达式、三元表达式、列表绑定、状态容器和条件样式。
5. 支持统一事件模型：Toast、Navigate、Track。
6. 提供 Android Demo，一端可展示合法配置、错误配置和样例切换（默认主场景为 `retention_dialog`）。
7. 提供 4 个合法样例和 4 个错误样例。
8. 提供 README、DSL 说明、设计说明、验证记录、提交清单和 AI 使用记录。

## 目录

```text
composeflow-camp/
  androidApp/   Android 可运行 Demo 壳
  shared/       KMP shared 模块，包含 DSL/Parser/IR/Renderer
  samples/      合法和错误 DSL 样例
  docs/         设计、DSL、验证、提交说明
  tools/        本地样例校验脚本
```

## 运行 Android Demo

如果本机已有 Android SDK，并且能访问 Maven Central / Google Maven：

```bash
bash ./gradlew :androidApp:installDebug
```

或在 Android Studio 中打开 `composeflow-camp`，选择 `androidApp` 运行。

Demo 支持：

- 合法 DSL 样例切换。
- 错误 DSL 样例切换。
- Parser 错误面板。
- 动态页面渲染。
- 点击按钮后的事件占位反馈。

## 运行测试

默认通过仓库内 Gradle Wrapper 执行：

```bash
bash ./gradlew :shared:jvmTest
```

不依赖 Android SDK 的样例校验：

```bash
python tools/validate_samples.py
```

## 样例

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

## 核心代码入口

- `DynamicDslParser.parse(raw)`：解析 JSON，返回 `ParseResult.Success` 或 `ParseResult.Failure`。
- `DynamicPage` / `DynamicNode` / `UiStyle`：类型安全 IR。
- `BindingResolver`：处理数据绑定、布尔判断、比较和三元表达式。
- `DynamicPageRenderer()`：把 IR 渲染为 Compose UI。
- `EventDispatcher.dispatch()`：统一处理 Toast、Navigate、Track。

## 答辩讲法

一句话版本：我把“剪映订阅挽留弹窗”里运营高频变化的结构、文案、样式、列表和事件从端代码中抽出来，用受控 DSL 表达，再通过 Parser 校验成类型安全 IR，最后由 Compose Renderer 统一渲染，从而让页面能跟随配置和数据变化，同时保留错误提示、工程边界和可测试性。
