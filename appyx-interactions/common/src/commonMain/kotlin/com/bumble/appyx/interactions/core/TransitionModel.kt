package com.bumble.appyx.interactions.core

import kotlinx.coroutines.flow.StateFlow

interface TransitionModel<NavTarget, NavState> {
    /**
     * 0..infinity
     */
    val maxProgress: Float

    /**
     * 0..infinity
     */
    val currentProgress: Float
        get() = with(segments.value) { index + progress }

    val segments: StateFlow<Segment<NavTarget, NavState>>

    class Segment<NavTarget, NavState>(
        val index: Int,
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
