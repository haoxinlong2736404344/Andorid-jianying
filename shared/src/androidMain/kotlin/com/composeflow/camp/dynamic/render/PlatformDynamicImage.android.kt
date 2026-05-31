package com.composeflow.camp.dynamic.render

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.composeflow.camp.dynamic.model.UiStyle
import com.composeflow.camp.shared.R

@Composable
internal actual fun PlatformDynamicImage(
    url: String,
    description: String?,
    style: UiStyle?,
    modifier: Modifier,
) {
    val resourceId = resolveDrawable(url, description)
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(resourceId),
            contentDescription = description,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        if (url.contains("benefit_icon")) {
            Text(
                text = description.orEmpty(),
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
    }
}

private fun resolveDrawable(url: String, description: String?): Int {
    return when {
        url.contains("retention_hero") -> R.drawable.retention_hero
        description == "4K" -> R.drawable.benefit_4k
        description == "BGM" -> R.drawable.benefit_bgm
        description == "AI" -> R.drawable.benefit_ai
        url.contains("benefit_icon") -> R.drawable.benefit_default
        else -> R.drawable.image_fallback
    }
}
