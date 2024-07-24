package com.bumble.appyx.utils.interop.coroutines.store

import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.store.RetainedInstanceStore
import com.bumble.appyx.navigation.store.getRetainedInstance
import kotlinx.coroutines.Job

/**
 * Obtains or creates an instance of a class via the [get] extension.
 * The Job will be cancelled when the disposer function is called.
 */
inline fun <reified T : Job> RetainedInstanceStore.getJob(
    storeId: String,
    key: String,
    noinline factory: () -> T
): T = get(
    storeId = storeId,
    disposer = { it.cancel() },
    factory = factory,
    key = key,
)

/**
 * Obtains or creates an instance of a class via the [getRetainedInstance] extension.
 * The Job will be cancelled when the disposer function is called.
 */
inline fun <reified T : Job> NodeContext.getRetainedDisposable(
    key: String,
    noinline factory: () -> T
) = getRetainedInstance(
    key = key,
    disposer = { it.cancel() },
    factory = factory,
)
