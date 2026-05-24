# AI Coding 使用记录

| 任务 | AI 工具 | 提示意图 | 采纳内容 | 拒绝内容 | 人工修改 | 验证方式 | 剩余风险 |
| --- | --- | --- | --- | --- | --- | --- | --- |
| DSL 字段与边界设计（选题一） | Codex | 先满足训练营 P0，再补 P1 可选能力 | 页面树、基础组件、样式、事件、数据绑定字段 | 一次性引入复杂脚本表达式 | 收敛为受控表达式（变量/比较/三元）并补边界文档 | 人工审阅 DSL 样例 + Parser 错误样例 | 远端配置平台能力未纳入本期 |
| Parser 与 IR 建模 | Codex | 从 JSON 转类型安全模型并输出可定位错误 | `DynamicPage` / `DynamicNode` / `UiStyle`、`ParseResult` 与错误路径 | 弱类型 `Map<String, Any>` 透传方案 | 改为强类型节点 + 枚举校验 + 非法颜色校验 | `tools/validate_samples.py`、`DynamicDslParserTest` | DSL 版本升级仍需迁移策略 |
| Renderer 与动态图片能力 | Codex | 映射 IR 到 Compose 并保证动态能力可扩展 | Column/Row/Box/Text/Image/Button/ForEach/StateLayout 渲染骨架 | 直接在 shared 层耦合平台 API | 抽出 `PlatformDynamicImage` expect/actual；Android 支持 `asset://` 与网络 URL，其他平台保留占位 | Android Demo 手动切换样例验证 + 代码 Review | iOS 端图片仍为占位，需要后续 actual 实现 |
| Demo 与样例组织 | Codex | 提供可答辩演示链路（成功+失败） | `retention_dialog` 主样例 + 扩展样例 + invalid 样例切换 | 仅保留单一样例 | 增加错误面板、样例分组、主场景优先展示 | Android Demo 手动演示：成功渲染/错误提示/事件反馈 | 真机兼容性需在完整设备矩阵回归 |
| 测试与验证补强 | Codex | 增加选题一专属证据 | retention_dialog 解析与按钮事件断言、绑定表达式测试 | 仅保留通用 smoke test | 新增关键断言并补缺失绑定策略（缺失路径输出空串） | `DynamicDslParserTest`、`BindingResolverTest`、`tools/validate_samples.py` | Gradle 依赖解析受网络环境影响可能失败 |
