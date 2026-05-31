package com.composeflow.camp.dynamic.render

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.composeflow.camp.dynamic.model.UiStyle

@Composable
internal actual fun PlatformDynamicImage(
    url: String,
    description: String?,
    style: UiStyle?,
    modifier: Modifier,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text = description ?: url,
            fontSize = 12.sp,
            color = Color(0xFF666666),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
    }
}
