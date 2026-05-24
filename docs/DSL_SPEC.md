# DSL 设计说明

## 顶层结构

```json
{
  "version": "1.0",
  "metadata": {
    "scene": "subscription_page"
  },
  "root": {
    "type": "Column",
    "children": []
  }
}
```

- `version`：必填，用于后续 DSL 兼容和灰度。
- `metadata`：可选，只放调试、归因、场景说明等非渲染字段。
- `root`：必填，页面根节点。

## 基础组件

### Column / Row / Box

容器组件，用于描述页面树。

```json
{
  "type": "Column",
  "style": { "padding": { "all": 16 } },
  "children": []
}
```

### Text

文本组件，`text` 必填。

```json
{
  "type": "Text",
  "text": "你好 { user.name }"
}
```

### Image

图片组件，`url` 必填。当前代码提供跨平台占位渲染，真实图片加载留给平台资源层。

```json
{
  "type": "Image",
  "url": "asset://empty_benefits.png",
  "description": "empty state"
}
```

### Button

按钮组件，`text` 必填，`action` 可选。

```json
{
  "type": "Button",
  "text": "立即开通",
  "action": {
    "type": "Navigate",
    "payload": { "route": "checkout" }
  }
}
```

## 进阶组件

### ForEach

列表绑定组件，`items` 指向输入数据中的数组路径，`itemName` 定义循环变量名。

```json
{
  "type": "ForEach",
  "items": "benefits.items",
  "itemName": "benefit",
  "children": [
    { "type": "Text", "text": "{ benefit.title }" }
  ]
}
```

### StateLayout

状态容器，根据 `state` 的结果渲染不同分支。

```json
{
  "type": "StateLayout",
  "state": "{ benefits.state }",
  "loading": [{ "type": "Text", "text": "加载中" }],
  "empty": [{ "type": "Text", "text": "暂无权益" }],
  "error": [{ "type": "Text", "text": "加载失败" }],
  "content": [{ "type": "Text", "text": "权益列表" }]
}
```

`state` 约定值：

- `loading`
- `empty`
- `error`
- 其他值默认走 `content`

## 样式

```json
{
  "width": 320,
  "height": 48,
  "backgroundColor": "#111111",
  "textColor": "#FFFFFF",
  "fontSize": 16,
  "cornerRadius": 24,
  "padding": { "horizontal": 16, "vertical": 12 },
  "margin": { "bottom": 16 },
  "horizontalAlignment": "Center",
  "verticalAlignment": "Center"
}
```

- 颜色只接受 `#RRGGBB` 或 `#AARRGGBB`。
- 尺寸单位约定为 dp。
- `padding` / `margin` 支持 `all`、`horizontal`、`vertical`、`start`、`top`、`end`、`bottom`。
- 对齐枚举：横向 `Start` / `Center` / `End`，纵向 `Top` / `Center` / `Bottom`。

## 条件样式

`styleWhen` 用于根据表达式覆盖基础样式。

```json
{
  "type": "Row",
  "style": { "backgroundColor": "#FFFFFFFF" },
  "styleWhen": [
    {
      "when": "{ benefit.status == 'used' }",
      "style": { "backgroundColor": "#FFECECEC" }
    }
  ]
}
```

## 数据绑定与表达式

文本中可以用 `{ path.to.value }` 读取输入数据：

```json
{ "type": "Text", "text": "你好 { user.name }" }
```

当前支持：

- 变量读取：`{ user.name }`
- 布尔读取：`{ user.isVip }`
- 比较：`{ benefits.count == 0 }`、`{ plan.type != 'trial' }`
- 简单三元：`{ user.isVip ? 'VIP' : 'Guest' }`

## 事件

统一事件模型避免 DSL 直接调用平台 API。

```json
{
  "type": "Track",
  "payload": {
    "event": "retain_subscribe_click"
  }
}
```

支持事件：

- `Toast`：展示提示。
- `Navigate`：跳转占位。
- `Track`：埋点占位。

## 版本策略

- `1.x`：保持字段兼容，只新增可选字段。
- `2.x`：允许调整字段语义或删除旧能力。
- Parser 应在灰度期同时支持旧版本，Renderer 只消费稳定 IR。
