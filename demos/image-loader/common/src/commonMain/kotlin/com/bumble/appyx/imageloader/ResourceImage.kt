package com.bumble.appyx.imageloader

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.resource

@Suppress("TooGenericExceptionCaught", "SwallowedException")
@OptIn(ExperimentalResourceApi::class)
@Composable
fun ResourceImage(
    path: String,
    modifier: Modifier = Modifier,
    fallbackUrl: String = path,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Fit
) {
    var image: ImageBitmap? by remember { mutableStateOf(null) }
    LaunchedEffect(Unit) {
        image = withContext(Dispatchers.Default) {
            try {
                resource(path)
                    .readBytes()
                    .toImageBitmap()
            } catch (e: Throwable) {
                try {
                    resource(fallbackUrl)
                        .readBytes()
                        .toImageBitmap()
                } catch (e: Throwable) {
                    null
                }
            }
        }
    }
    image?.let {
        Image(
            bitmap = it,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale
        )
    }
}
