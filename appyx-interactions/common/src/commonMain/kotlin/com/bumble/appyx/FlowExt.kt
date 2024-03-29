package com.bumble.appyx

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
fun <T, R> StateFlow<T>.mapState(
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
inline fun <reified T, R> combineState(
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

inline fun <reified T1, reified T2, R> StateFlow<T1>.combineState(
    flow: StateFlow<T2>,
    scope: CoroutineScope,
    sharingStarted: SharingStarted = SharingStarted.Eagerly,
    crossinline transform: (T1, T2) -> R,
): StateFlow<R> =
    combine(this, flow) { one, two -> transform(one, two) }
        .stateIn(
            scope = scope,
            started = sharingStarted,
            initialValue = transform(this.value, flow.value)
        )

fun <T> Flow<T>.withPrevious(): Flow<CompareValues<T>> =
    scan(CompareValues<T>()) { previous, current -> previous.combine(current) }
        .filter { it.isInitialized }

class CompareValues<T>(
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
