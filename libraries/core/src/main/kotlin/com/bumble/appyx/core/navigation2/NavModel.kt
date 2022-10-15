package com.bumble.appyx.core.navigation2

import kotlinx.coroutines.flow.StateFlow

interface NavModel<NavTarget, NavState> {
    /**
     * 0..infinity
     */
    val maxProgress: Float

    /**
     * 0..infinity
     */
    val currentProgress: Float
        get() = elements.value.segmentIndex + elements.value.progress

    val elements: StateFlow<Segment<NavTarget, NavState>>

    class Segment<NavTarget, NavState>(
        val segmentIndex: Int,
        val navTransition: NavTransition<NavTarget, NavState>,
        /**
         * 0..1
         */
        val progress: Float
    )

    fun enqueue(operation: Operation<NavTarget, NavState>): Boolean

    fun setProgress(progress: Float)

    fun dropAfter(segmentIndex: Int)
}
