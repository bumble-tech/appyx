package com.bumble.appyx.core.navigation2

import kotlinx.coroutines.flow.StateFlow

interface NavModel<NavTarget, NavState> {
    /**
     * 0..infinity
     */
    val maxProgress: Float

    val elements: StateFlow<State<NavTarget, NavState>>

    class State<NavTarget, NavState>(
        val segmentIndex: Int,
        val navTransition: NavTransition<NavTarget, NavState>,
        val progress: Float
    )

    fun enqueue(operation: Operation<NavTarget, NavState>)

    fun setProgress(progress: Float)
}
