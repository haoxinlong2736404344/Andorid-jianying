package com.composeflow.camp.dynamic.render

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun PlatformDynamicImage(
    url: String,
    description: String?,
    modifier: Modifier,
    fallbackText: String,
)
