package com.bumble.appyx.core.navigation2

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlin.coroutines.EmptyCoroutineContext

abstract class BaseNavModel<NavTarget, State>(
    private val finalStates: Set<State> = setOf(),
    protected val scope: CoroutineScope = CoroutineScope(EmptyCoroutineContext + Dispatchers.Unconfined)
) {
    abstract val initialState: NavElements<NavTarget, State>

    /**
     * A state queue that can be prefetched. Could also work as state history if we implement
     * reversing progress.
     */
    private val queue: MutableList<NavTransition<NavTarget, State>> by lazy {
        mutableListOf(
            NavTransition(
                fromState = initialState,
                targetState = initialState,
            )
        )
    }

    /**
     * 0..infinity
     */
    private var lastRecordedProgress: Float = 0f

    /**
     * 0..infinity
     */
    val maxProgress: Float
        get() = (queue.lastIndex + 1).toFloat()

    class State<Target, State>(
        val segmentIndex: Int,
        val navTransition: NavTransition<Target, State>,
        val progress: Float
    )

    private val state: MutableStateFlow<BaseNavModel.State<NavTarget, State>> by lazy {
        MutableStateFlow(createState(lastRecordedProgress))
    }

    val elements: StateFlow<BaseNavModel.State<NavTarget, State>> by lazy {
        state
    }

    private fun createState(progress: Float): BaseNavModel.State<NavTarget, State> {
        /**
         *  Normally progress on any segment is a half-open interval: [0%, 100), so that
         *  100% is interpreted as 0% of the next segment.
         *  The only case when this won't work is when we reach the very end of possible progress,
         *  then there's no next segment to go to. Instead, let's interpret it as 100% of the last
         *  segment.
         */
        val segmentIndex = (if (progress == maxProgress) (progress - 1) else progress).toInt()

        return State(
            segmentIndex = segmentIndex,
            navTransition = queue[segmentIndex],
            progress = progress - segmentIndex
        )
    }

    fun enqueue(operation: Operation<NavTarget, State>) {
        val newState = operation.invoke(queue.last().targetState)
        queue.add(newState)
    }

    fun setProgress(progress: Float) {
        Log.d("Progress update", "$progress") // FIXME remove
        if (progress.toInt() > lastRecordedProgress.toInt()) {
            // TODO uncomment when method is merged here
            //  com.bumble.appyx.core.navigation.BaseNavModel.onTransitionFinished
            // onTransitionFinished(state.value.fromState.map { it.key })
            Log.d("Transition finished", "onTransitionFinished()")
        }

        lastRecordedProgress = progress
        state.update { createState(progress) }
    }
}
