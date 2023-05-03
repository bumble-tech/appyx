package com.bumble.appyx.dagger.hilt

import com.bumble.appyx.core.modality.AncestryInfo
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node

interface NodeFactoryProviderAware {
    val nodeFactoryProvider: NodeFactoryProvider
}

fun Node.getNodeFactoryProvider(): NodeFactoryProvider =
    if (this is NodeFactoryProviderAware) {
        this.nodeFactoryProvider
    } else {
        checkNotNull(parent?.getNodeFactoryProvider()) {
            "No nodes in the node tree implement HiltNodeFactoryProvider"
        }
    }

fun BuildContext.getNodeFactoryProvider(): NodeFactoryProvider =
    ancestryInfo.let { ancestryInfo ->
        check(ancestryInfo is AncestryInfo.Child) {
            "Finding HiltNodeFactoryProvider on the root BuildContext is not possible"
        }
        ancestryInfo.anchor.getNodeFactoryProvider()
    }

inline fun <reified T> BuildContext.getAggregateNodeFactory(): T =
    getNodeFactoryProvider().getAggregateNodeFactory()
