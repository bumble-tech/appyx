package com.bumble.appyx.navigation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale

@Composable
expect fun EmbeddableResourceImage(
    path: String,
    contentDescription: String,
    contentScale: ContentScale,
    modifier: Modifier,
)
