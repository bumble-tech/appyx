package com.bumble.appyx.utils.interop.rx3.store

import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.store.RetainedInstanceStore
import com.bumble.appyx.navigation.store.get
import com.bumble.appyx.navigation.store.getRetainedInstance
import io.reactivex.rxjava3.disposables.Disposable

/**
 * Obtains or creates an instance of a class via the [get] extension.
 * The disposable will be disposed when the disposer function is called.
 */
inline fun <reified T : Disposable> RetainedInstanceStore.getDisposable(
    storeId: String,
    noinline factory: () -> T
): T =
    get(
        storeId = storeId,
        disposer = { it.dispose() },
        factory = factory
    )

/**
 * Obtains or creates an instance of a class via the [getRetainedInstance] extension.
 * The disposable will be disposed when the disposer function is called.
 *
 * If you need multiple instances of an object with the same key, do not use this extension.
 */
inline fun <reified T : Disposable> NodeContext.getRetainedDisposable(
    noinline factory: () -> T
) =
    getRetainedInstance(disposer = { it.dispose() }, factory = factory)

/**
 * Obtains or creates an instance of a class via the [getRetainedInstance] extension.
 * The disposable will be disposed when the disposer function is called.
 */
inline fun <reified T : Disposable> NodeContext.getRetainedDisposable(
    key: String,
    noinline factory: () -> T
) =
    getRetainedInstance(key = key, disposer = { it.dispose() }, factory = factory)
