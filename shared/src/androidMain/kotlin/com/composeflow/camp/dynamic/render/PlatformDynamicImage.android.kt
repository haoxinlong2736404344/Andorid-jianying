package com.composeflow.camp.dynamic.render

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage

@Composable
actual fun PlatformDynamicImage(
    url: String,
    description: String?,
    modifier: Modifier,
    fallbackText: String,
) {
    val context = LocalContext.current
    val drawableId = url.toDrawableId(context.packageName, context.resources)
    when {
        drawableId != 0 -> Image(
            painter = painterResource(drawableId),
            contentDescription = description,
            modifier = modifier,
            contentScale = ContentScale.Crop,
        )

        url.startsWith("http://") || url.startsWith("https://") || url.startsWith("file://") || url.startsWith("content://") -> AsyncImage(
            model = url,
            contentDescription = description,
            modifier = modifier,
            contentScale = ContentScale.Crop,
        )

        else -> Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text(text = fallbackText)
        }
    }
}

private fun String.toDrawableId(packageName: String, resources: android.content.res.Resources): Int {
    if (!startsWith("asset://")) return 0
    val rawName = removePrefix("asset://").substringBeforeLast(".")
    return resources.getIdentifier(rawName, "drawable", packageName)
}
