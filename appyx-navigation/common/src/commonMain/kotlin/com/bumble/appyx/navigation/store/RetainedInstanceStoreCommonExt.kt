package com.bumble.appyx.navigation.store

import com.bumble.appyx.navigation.modality.NodeContext

/**
 * Obtains or creates an instance of a class using the identifier from the [NodeContext].
 */
inline fun <reified T : Any> NodeContext.getRetainedInstance(
    key: String,
    noinline disposer: (T) -> Unit = {},
    noinline factory: () -> T
) =
    RetainedInstanceStore.get(
        storeId = identifier,
        key = key,
        disposer = disposer,
        factory = factory
    )
