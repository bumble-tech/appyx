package com.bumble.appyx.v2.core

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.scan

@OptIn(ExperimentalCoroutinesApi::class)
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

}
