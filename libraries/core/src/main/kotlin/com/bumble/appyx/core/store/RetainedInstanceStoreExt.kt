package com.bumble.appyx.core.store

import com.bumble.appyx.core.modality.BuildContext

/**
 * Obtains or creates an instance of a class using the class name as the key.
 * If you need multiple instances of an object with the same key, do not use this extension.
 */
inline fun <reified T : Any> RetainedInstanceStore.get(
    storeId: String,
    noinline disposer: (T) -> Unit = {},
    noinline factory: () -> T
): T =
    get(
        storeId = storeId,
        key = T::class.java.name,
        disposer = disposer,
        factory = factory
    )

/**
 * Obtains or creates an instance of a class using the class name as the key, and uses the
 * identifier from the BuildContext.
 *
 * If you need multiple instances of an object with the same key, do not use this extension.
 */
inline fun <reified T : Any> BuildContext.getRetainedInstance(
    noinline disposer: (T) -> Unit = {},
    noinline factory: () -> T
) =
    RetainedInstanceStore.get(
        storeId = identifier,
        disposer = disposer,
        factory = factory
    )

/**
 * Obtains or creates an instance of a class using the identifier from the BuildContext.
 */
inline fun <reified T : Any> BuildContext.getRetainedInstance(
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
