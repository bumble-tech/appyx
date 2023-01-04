package com.bumble.appyx.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn

/**
 * Maps [StateFlow] into [StateFlow] in synchronous manner without having to provide a default value manually.
 *
 * Issues in Kotlin bug tracker:
 * [#2008](https://github.com/Kotlin/kotlinx.coroutines/issues/2008)
 * [#2514](https://github.com/Kotlin/kotlinx.coroutines/issues/2514)
 * [#2631](https://github.com/Kotlin/kotlinx.coroutines/issues/2631)
 */
internal fun <T, R> StateFlow<T>.mapState(
    scope: CoroutineScope,
    sharingStarted: SharingStarted = SharingStarted.Eagerly,
    mapper: (T) -> R,
) =
    map { mapper(it) }
        .stateIn(scope, sharingStarted, mapper(value))

/**
 * Combines [StateFlow] into [StateFlow] in synchronous manner without having to provide a default value manually.
 *
 * Issues in Kotlin bug tracker:
 * [#2008](https://github.com/Kotlin/kotlinx.coroutines/issues/2008)
 * [#2514](https://github.com/Kotlin/kotlinx.coroutines/issues/2514)
 * [#2631](https://github.com/Kotlin/kotlinx.coroutines/issues/2631)
 */
internal inline fun <reified T, R> combineState(
    flows: Iterable<StateFlow<T>>,
    scope: CoroutineScope,
    sharingStarted: SharingStarted = SharingStarted.Eagerly,
    crossinline transform: (Array<T>) -> R,
): StateFlow<R> =
    combine(flows) { transform(it) }
        .stateIn(
            scope = scope,
            started = sharingStarted,
            initialValue = transform(flows.map { it.value }.toTypedArray())
        )

internal fun <T> Flow<T>.withPrevious(): Flow<CompareValues<T>> =
    scan(CompareValues<T>()) { previous, current -> previous.combine(current) }
        .filter { it.isInitialized }

internal class CompareValues<T>(
    val previous: T? = null,
    private val currentNullable: T? = null,
) {
    val current: T
        get() = currentNullable ?: error("Should not be invoked")

    val isInitialized: Boolean
        get() = currentNullable != null

    fun combine(new: T): CompareValues<T> =
        CompareValues(currentNullable, new)

    operator fun component1(): T? = previous

    operator fun component2(): T = current

}
