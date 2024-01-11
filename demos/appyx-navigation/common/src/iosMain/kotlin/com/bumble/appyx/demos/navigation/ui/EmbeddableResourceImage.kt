package com.bumble.appyx.demos.navigation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.bumble.appyx.imageloader.ResourceImage

@Composable
actual fun EmbeddableResourceImage(
    path: String,
    contentDescription: String,
    contentScale: ContentScale,
    modifier: Modifier
) {
    ResourceImage(
        path = path,
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier,
    )
}
