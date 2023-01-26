package com.bumble.appyx.interactions.core

import kotlinx.coroutines.flow.StateFlow

interface TransitionModel<NavTarget, ModelState> {

    /**
     * 0..infinity
     */
    val maxProgress: Float

    /**
     * 0..infinity
     */
    val currentProgress: Float
        get() = with(segments.value) { index + progress }

    val segments: StateFlow<Segment<ModelState>>

    class Segment<ModelState>(
        val index: Int,
        val navTransition: NavTransition<ModelState>,
        /**
         * 0..1
         */
        val progress: Float
    )

    fun enqueue(operation: Operation<ModelState>): Boolean

    fun updateState(operation: Operation<ModelState>): Boolean

    fun setProgress(progress: Float)

    fun dropAfter(segmentIndex: Int)
}
