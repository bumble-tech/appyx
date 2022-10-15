package com.bumble.appyx.core.navigation2

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlin.coroutines.EmptyCoroutineContext

@SuppressWarnings("UnusedPrivateMember")
abstract class BaseNavModel<NavTarget, NavState>(
    private val finalStates: Set<NavState> = setOf(),
    protected val scope: CoroutineScope = CoroutineScope(EmptyCoroutineContext + Dispatchers.Unconfined)
) : NavModel<NavTarget, NavState> {
    abstract val initialState: NavElements<NavTarget, NavState>

    /**
     * A state queue that can be prefetched. Could also work as state history if we implement
     * reversing progress.
     */
    private val queue: MutableList<NavTransition<NavTarget, NavState>> by lazy {
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
    private var lastRecordedProgress: Float = 1f

    /**
     * 0..infinity
     */
    override val maxProgress: Float
        get() = (queue.lastIndex + 1).toFloat()

    private val state: MutableStateFlow<NavModel.Segment<NavTarget, NavState>> by lazy {
        MutableStateFlow(createState(lastRecordedProgress))
    }

    override val segments: StateFlow<NavModel.Segment<NavTarget, NavState>> by lazy {
        state
    }

    private fun createState(progress: Float): NavModel.Segment<NavTarget, NavState> {
        val progress = progress.coerceAtLeast(1f)

        /**
         *  Normally progress on any segment is a half-open interval: [0%, 100), so that
         *  100% is interpreted as 0% of the next segment.
         *  The only case when this won't work is when we reach the very end of possible progress,
         *  then there's no next segment to go to. Instead, let's interpret it as 100% of the last
         *  segment.
         */
        val segmentIndex = (if (progress == maxProgress) (progress - 1) else progress).toInt()

        return NavModel.Segment(
            index = segmentIndex,
            navTransition = queue[segmentIndex],
            progress = progress - segmentIndex
        )
    }

    override fun enqueue(operation: Operation<NavTarget, NavState>): Boolean {
        val baseline = queue.last().targetState

        return if (operation.isApplicable(baseline)) {
            val newState = operation.invoke(baseline)
            queue.add(newState)
            true
        } else {
            Log.d("BaseNavModel", "Operation $operation is not applicable on state: $baseline")
            false
        }
    }

    override fun setProgress(progress: Float) {
        val progress = progress.coerceAtLeast(1f)
        Log.d("BaseNavModel", "Progress update: $progress")
        if (progress.toInt() > lastRecordedProgress.toInt()) {
            // TODO uncomment when method is merged here
            //  com.bumble.appyx.core.navigation.BaseNavModel.onTransitionFinished
            // onTransitionFinished(state.value.fromState.map { it.key })
            Log.d("Transition finished", "onTransitionFinished()")
        }

        lastRecordedProgress = progress
        state.update { createState(progress) }
    }

    override fun dropAfter(segmentIndex: Int) {
        while (segmentIndex < queue.size) {
            queue.removeLast()
        }
    }
}
