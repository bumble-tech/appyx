package com.bumble.appyx.demos.navigation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.bumble.appyx.imageloader.ResourceImage

private const val EMBED_URL_PATH = "appyx/samples/documentation-appyx-navigation/"

@Composable
actual fun EmbeddableResourceImage(
    path: String,
    contentDescription: String,
    contentScale: ContentScale,
    modifier: Modifier
) {
    ResourceImage(
        path = EMBED_URL_PATH + path,
        fallbackUrl = path,
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier,
    )
}
