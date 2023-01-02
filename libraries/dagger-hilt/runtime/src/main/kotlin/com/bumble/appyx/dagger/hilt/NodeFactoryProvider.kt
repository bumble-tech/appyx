package com.bumble.appyx.dagger.hilt

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.bumble.appyx.core.integration.NodeFactory
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.dagger.hilt.internal.LazyNodeFactory
import com.bumble.appyx.dagger.hilt.internal.getHiltEntrypoint

@Stable
interface NodeFactoryProvider {
    fun <T> getAggregateNodeFactory(clazz: Class<T>): T
    fun <T : Node> getNodeFactory(clazz: Class<T>): NodeFactory<T>
    fun <T> getCustomNodeFactory(clazz: Class<T>): T
}

inline fun <reified T> NodeFactoryProvider.getAggregateNodeFactory(): T =
    getAggregateNodeFactory(T::class.java)

inline fun <reified T : Node> NodeFactoryProvider.getNodeFactory(): NodeFactory<T> =
    LazyNodeFactory(lazy { getNodeFactory(T::class.java) })

inline fun <reified T> NodeFactoryProvider.getCustomNodeFactory(): T =
    getCustomNodeFactory(T::class.java)

@Composable
fun rememberNodeFactoryProvider(): NodeFactoryProvider {
    val context = LocalContext.current
    return remember(context) { getHiltEntrypoint(context).nodeFactoryProvider }
}
