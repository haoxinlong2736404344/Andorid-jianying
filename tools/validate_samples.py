from __future__ import annotations

import json
import re
from pathlib import Path
from typing import Any


ROOT = Path(__file__).resolve().parents[1]
VALID_TYPES = {"Column", "Row", "Box", "Text", "Image", "Button", "ForEach", "StateLayout"}
VALID_EVENTS = {"Toast", "Navigate", "Track"}
VALID_ALIGN_X = {"Start", "Center", "End"}
VALID_ALIGN_Y = {"Top", "Center", "Bottom"}
VALID_FONT_WEIGHT = {"Normal", "Medium", "Bold"}
VALID_TEXT_ALIGN = {"Start", "Center", "End"}
COLOR_RE = re.compile(r"^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{8})$")


def validate_page(page: dict[str, Any]) -> list[str]:
    errors: list[str] = []
    if not isinstance(page.get("version"), str):
        errors.append("$.version 缺少或不是字符串")
    if "root" not in page:
        errors.append("$.root 缺少")
    else:
        validate_node(page["root"], "$.root", errors)
    return errors


def validate_node(node: Any, path: str, errors: list[str]) -> None:
    if not isinstance(node, dict):
        errors.append(f"{path} 必须是对象")
        return
    node_type = node.get("type")
    if node_type not in VALID_TYPES:
        errors.append(f"{path}.type 未知组件类型: {node_type}")
        return
    if node_type == "Text" and not isinstance(node.get("text"), str):
        errors.append(f"{path}.text Text 必须提供 text")
    if node_type == "Image" and not isinstance(node.get("url"), str):
        errors.append(f"{path}.url Image 必须提供 url")
    if node_type == "Button":
        if not isinstance(node.get("text"), str):
            errors.append(f"{path}.text Button 必须提供 text")
        validate_action(node.get("action"), f"{path}.action", errors)
    if node_type == "ForEach" and not isinstance(node.get("items"), str):
        errors.append(f"{path}.items ForEach 必须提供 items")
    if node_type == "StateLayout" and not isinstance(node.get("state"), str):
        errors.append(f"{path}.state StateLayout 必须提供 state")
    validate_style(node.get("style"), f"{path}.style", errors)
    validate_style_when(node.get("styleWhen"), f"{path}.styleWhen", errors)
    for index, child in enumerate(node.get("children", [])):
        validate_node(child, f"{path}.children[{index}]", errors)
    for branch in ("loading", "empty", "error", "content"):
        for index, child in enumerate(node.get(branch, [])):
            validate_node(child, f"{path}.{branch}[{index}]", errors)


def validate_style(style: Any, path: str, errors: list[str]) -> None:
    if style is None:
        return
    if not isinstance(style, dict):
        errors.append(f"{path} 必须是对象")
        return
    for key in ("backgroundColor", "textColor"):
        if key in style and not COLOR_RE.match(str(style[key])):
            errors.append(f"{path}.{key} 非法颜色: {style[key]}")
    if "borderColor" in style and not COLOR_RE.match(str(style["borderColor"])):
        errors.append(f"{path}.borderColor 非法颜色: {style['borderColor']}")
    if "horizontalAlignment" in style and style["horizontalAlignment"] not in VALID_ALIGN_X:
        errors.append(f"{path}.horizontalAlignment 非法")
    if "verticalAlignment" in style and style["verticalAlignment"] not in VALID_ALIGN_Y:
        errors.append(f"{path}.verticalAlignment 非法")
    if "fontWeight" in style and style["fontWeight"] not in VALID_FONT_WEIGHT:
        errors.append(f"{path}.fontWeight 非法")
    if "textAlign" in style and style["textAlign"] not in VALID_TEXT_ALIGN:
        errors.append(f"{path}.textAlign 非法")


def validate_style_when(style_when: Any, path: str, errors: list[str]) -> None:
    if style_when is None:
        return
    if not isinstance(style_when, list):
        errors.append(f"{path} 必须是数组")
        return
    for index, item in enumerate(style_when):
        item_path = f"{path}[{index}]"
        if not isinstance(item, dict):
            errors.append(f"{item_path} 必须是对象")
            continue
        if not isinstance(item.get("when"), str):
            errors.append(f"{item_path}.when 必须是字符串")
        validate_style(item.get("style"), f"{item_path}.style", errors)


def validate_action(action: Any, path: str, errors: list[str]) -> None:
    if action is None:
        return
    if not isinstance(action, dict):
        errors.append(f"{path} 必须是对象")
        return
    if action.get("type") not in VALID_EVENTS:
        errors.append(f"{path}.type 未知事件类型: {action.get('type')}")


def main() -> int:
    failed = False
    for kind in ("valid", "invalid"):
        for sample in sorted((ROOT / "samples" / kind).glob("*.json")):
            page = json.loads(sample.read_text(encoding="utf-8"))
            errors = validate_page(page)
            if kind == "valid" and errors:
                failed = True
                print(f"FAIL {sample.relative_to(ROOT)}")
                for error in errors:
                    print(f"  - {error}")
            elif kind == "invalid" and not errors:
                failed = True
                print(f"FAIL {sample.relative_to(ROOT)}: expected validation errors")
            else:
                print(f"OK   {sample.relative_to(ROOT)}")
    return 1 if failed else 0


if __name__ == "__main__":
    raise SystemExit(main())
