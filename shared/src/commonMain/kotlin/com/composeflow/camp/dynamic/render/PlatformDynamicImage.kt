package com.composeflow.camp.dynamic.render

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.composeflow.camp.dynamic.model.UiStyle

@Composable
internal expect fun PlatformDynamicImage(
    url: String,
    description: String?,
    style: UiStyle?,
    modifier: Modifier,
)
