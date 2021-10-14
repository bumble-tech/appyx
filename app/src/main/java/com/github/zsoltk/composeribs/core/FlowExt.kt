package com.github.zsoltk.composeribs.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * Regular non-suspend version of [StateFlow.map] function
 * that allows [StateFlow] to [StateFlow] mapping.
 *
 * Lazily evaluates [mapper] without any caching mechanism,
 * as caching requires [CoroutineScope] to work and effectively cache the result.
 *
 * [GitHub issue](https://github.com/Kotlin/kotlinx.coroutines/issues/2008).
 */
@OptIn(InternalCoroutinesApi::class)
fun <T, R> StateFlow<T>.unsuspendedMap(mapper: (T) -> R): StateFlow<R> =
    object : StateFlow<R> {
        override val replayCache: List<R>
            get() = listOf(value)
        override val value: R
            get() = mapper(this@unsuspendedMap.value)

        @InternalCoroutinesApi
        override suspend fun collect(collector: FlowCollector<R>) {
            this@unsuspendedMap.map { mapper(it) }.distinctUntilChanged().collect(collector)
        }
    }
