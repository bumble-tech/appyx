package com.bumble.appyx.interactions.core

import com.bumble.appyx.interactions.Logger
import com.bumble.appyx.interactions.core.Operation.Mode.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlin.coroutines.EmptyCoroutineContext

@SuppressWarnings("UnusedPrivateMember")
abstract class BaseTransitionModel<NavTarget, ModelState>(
    protected val scope: CoroutineScope = CoroutineScope(EmptyCoroutineContext + Dispatchers.Unconfined)
) : TransitionModel<NavTarget, ModelState> {
    abstract val initialState: ModelState

    abstract fun ModelState.destroyedElements(): Set<NavElement<NavTarget>>

    abstract fun ModelState.availableElements(): Set<NavElement<NavTarget>>

    override fun availableElements(): Set<NavElement<NavTarget>> =
        state.value.navTransition.targetState.availableElements()

    /**
     * A state queue that can be prefetched. Could also work as state history if we implement
     * reversing progress.
     */
    private val queue: MutableList<NavTransition<ModelState>> by lazy {
        mutableListOf(
            initialTransition
        )
    }

    protected open val initialTransition: NavTransition<ModelState> by lazy {
        NavTransition(
            fromState = initialState,
            targetState = initialState,
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

    private val state: MutableStateFlow<TransitionModel.Segment<ModelState>> by lazy {
        MutableStateFlow(createState(lastRecordedProgress))
    }

    override val segments: StateFlow<TransitionModel.Segment<ModelState>> by lazy {
        state
    }

    private var enforcedMode: Operation.Mode? = null

    override fun relaxExecutionMode() {
        enforcedMode = null
    }

    private fun createState(progress: Float): TransitionModel.Segment<ModelState> {
        val progress = progress.coerceIn(minimumValue = 1f, maximumValue = maxProgress)

        /**
         *  Normally progress on any segment is a half-open interval: [0%, 100), so that
         *  100% is interpreted as 0% of the next segment.
         *  The only case when this won't work is when we reach the very end of possible progress,
         *  then there's no next segment to go to. Instead, let's interpret it as 100% of the last
         *  segment.
         */
        val segmentIndex = (if (progress == maxProgress) (progress - 1) else progress).toInt()

        return TransitionModel.Segment(
            index = segmentIndex,
            navTransition = queue[segmentIndex],
            progress = progress - segmentIndex,
            animate = enforcedMode == IMMEDIATE
        )
    }

    override fun operation(operation: Operation<ModelState>, overrideMode: Operation.Mode?): Boolean =
        when (enforcedMode ?: overrideMode ?: operation.mode) {
            IMMEDIATE -> {
                // Replacing in an active segment triggers IMMEDIATE mode as a side effect
                if (currentProgress > queue.lastIndex - 1) {
                    enforcedMode = IMMEDIATE
                }
                updateState(operation)
            }
            GEOMETRY -> {
                updateState(operation)
            }
            KEYFRAME -> {
                enqueue(operation)
            }
        }

    private fun enqueue(operation: Operation<ModelState>): Boolean {
        val baseline = queue.last().targetState

        return if (operation.isApplicable(baseline)) {
            val newState = operation.invoke(baseline)
            queue.add(newState)
            true
        } else {
            Logger.log(TAG, "Operation $operation is not applicable on state: $baseline")
            false
        }
    }

    private fun updateState(operation: Operation<ModelState>): Boolean {
        val latestState = queue.last()

        return if (operation.isApplicable(latestState.targetState)) {
            val newState = operation.invoke(latestState.targetState)
            queue[queue.lastIndex] = NavTransition(
                fromState = latestState.fromState,
                targetState = newState.targetState
            )
            state.update { createState(currentProgress) }
            true
        } else {
            Logger.log(TAG, "Operation $operation is not applicable on state: $latestState")
            false
        }
    }

    override fun setProgress(progress: Float) {
        val progress = progress.coerceIn(minimumValue = 1f, maximumValue = maxProgress)
        Logger.log(TAG, "Progress update: $progress")
        if (progress.toInt() > lastRecordedProgress.toInt()) {
            // TODO uncomment when method is merged here
            //  com.bumble.appyx.interactions.core.navigation.BaseNavModel.onTransitionFinished
            // onTransitionFinished(state.value.fromState.map { it.key })
            Logger.log(TAG, "onTransitionFinished()")
        }

        lastRecordedProgress = progress
        state.update { createState(progress) }
    }

    override fun dropAfter(segmentIndex: Int) {
        while (segmentIndex < queue.size) {
            queue.removeLast()
        }
    }

    private companion object {
        private val TAG = BaseTransitionModel::class.java.name
    }
}
