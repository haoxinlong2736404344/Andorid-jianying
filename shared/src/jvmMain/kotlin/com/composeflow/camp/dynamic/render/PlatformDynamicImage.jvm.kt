package com.composeflow.camp.dynamic.render

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
actual fun PlatformDynamicImage(
    url: String,
    description: String?,
    modifier: Modifier,
    fallbackText: String,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(text = fallbackText)
    }
}
