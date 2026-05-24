# AI Coding 使用记录

| 标题 | 内容 |
| --- | --- |
| 任务 | 根据训练营文档生成 KMP 动态化 UI 方案、Android Demo、代码、DSL 样例和说明文档 |
| AI 工具 | Codex |
| 提示意图 | 读取 Word 文档，提炼交付要求，并持续补齐到训练营要求全部满足 |
| 采纳内容 | Parser、IR、表达式绑定、事件模型、Renderer、Android Demo、样例配置、README、DSL 文档、设计文档、验证记录 |
| 拒绝内容 | 未生成远端配置平台、完整发布系统、复杂动画和真实图片加载，避免超出训练营周期和可解释范围 |
| 人工修改 | 后续真实业务接入时需要开发者补 Android/iOS 图片加载、真实跳转、真实埋点 SDK 和线上灰度策略 |
| 验证方式 | 使用 `tools/validate_samples.py` 验证合法/错误 DSL；解析 JSON/XML；如 Gradle 可用，执行 `:shared:jvmTest` 和 `:androidApp:installDebug` |
| 剩余风险 | 当前环境缺少 Gradle/Android SDK 验证能力；真实端侧运行需要在 Android Studio 或具备 Android SDK 的机器上完成 |
