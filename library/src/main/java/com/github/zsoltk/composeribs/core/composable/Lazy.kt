package com.github.zsoltk.composeribs.core.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyGridScope
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import com.github.zsoltk.composeribs.core.children.ChildEntry

fun <T> LazyListScope.childrenItemsIndexed(
    children: List<ChildEntry.Eager<T>>,
    itemContent: @Composable LazyItemScope.(index: Int, child: ChildEntry.Eager<T>) -> Unit = { _, child -> child.node.Compose() }
) {
    itemsIndexed(
        items = children,
        key = { _: Int, child: ChildEntry.Eager<T> -> child.key },
        itemContent = itemContent
    )
}

fun <T> LazyListScope.childrenItems(
    children: List<ChildEntry.Eager<T>>,
    itemContent: @Composable LazyItemScope.(child: ChildEntry.Eager<T>) -> Unit = { it.node.Compose() }
) {
    items(
        items = children,
        key = { child: ChildEntry.Eager<T> -> child.key },
        itemContent = itemContent
    )
}

@ExperimentalFoundationApi
fun <T> LazyGridScope.childrenItemsIndexed(
    children: List<ChildEntry.Eager<T>>,
    itemContent: @Composable LazyItemScope.(index: Int, child: ChildEntry.Eager<T>) -> Unit = { _, child -> child.node.Compose() }
) {
    itemsIndexed(
        items = children,
        itemContent = itemContent
    )
}

@ExperimentalFoundationApi
fun <T> LazyGridScope.childrenItems(
    children: List<ChildEntry.Eager<T>>,
    itemContent: @Composable LazyItemScope.(child: ChildEntry.Eager<T>) -> Unit = { it.node.Compose() }
) {
    items(
        items = children,
        itemContent = itemContent
    )
}
