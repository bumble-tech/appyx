package com.bumble.appyx.core.navigation2.model

import android.util.Log
import com.bumble.appyx.core.navigation.NavElements
import com.bumble.appyx.core.navigation.Operation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlin.coroutines.EmptyCoroutineContext

open class BaseNavModel<Target, State>(
    initialState: NavElements<Target, State>,
    protected val scope: CoroutineScope = CoroutineScope(EmptyCoroutineContext + Dispatchers.Unconfined)
) {
    /**
     * A state queue that can be prefetched. Could also work as state history if we implement
     * reversing progress.
     *
     * TODO com/bumble/appyx/core/navigation/BaseNavModel.kt:38 – private val finalStates
     *  can be dropped if instead we diff between consecutive states to calculate which elements
     *  are destroyed
     */
    private val queue: MutableList<NavElements<Target, State>> = mutableListOf(
        initialState
    )

    /**
     * 0..infinity
     */
    private var internalProgress: Float = 0f

    /**
     * 0..infinity
     */
    val maxProgress: Float
        get() = queue.lastIndex.toFloat()

    class State<Target, State>(
        val segmentIndex: Int,
        val fromState: NavElements<Target, State>,
        val targetState: NavElements<Target, State>,
        val progress: Float
    )

    private val state: MutableStateFlow<BaseNavModel.State<Target, State>> by lazy {
        MutableStateFlow(createState(internalProgress))
    }


    val elements: StateFlow<BaseNavModel.State<Target, State>> = state

    private fun createState(progress: Float): BaseNavModel.State<Target, State> {
        val segmentIndex = progress.toInt()

        return State(
            segmentIndex = segmentIndex,
            fromState = queue[segmentIndex],
            targetState = queue.getOrNull(segmentIndex + 1) ?: queue[segmentIndex],
            progress = progress - segmentIndex
        )
    }

    fun enqueue(operation: Operation<Target, State>) {
        val newState = operation.invoke(queue.last())
        queue.add(newState)
    }

    fun setProgress(progress: Float) {
        Log.d("Progress update", "$progress")
        if (progress.toInt() > internalProgress.toInt()) {
            // TODO uncomment when method is merged here
            //  com.bumble.appyx.core.navigation.BaseNavModel.onTransitionFinished
            // onTransitionFinished(state.value.fromState.map { it.key })
            Log.d("Transition finished", "onTransitionFinished()")
        }

        internalProgress = progress
        state.update { createState(progress) }
    }
}
