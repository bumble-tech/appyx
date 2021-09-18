package com.github.zsoltk.composeribs.core

import androidx.compose.ui.Modifier
import com.github.zsoltk.composeribs.core.routing.RoutingKey


data class ViewChildEntry<T>(
    val key: RoutingKey<*>,
    val composable: InnerNode<T>,
    val modifier: Modifier = Modifier, // TODO var
)
