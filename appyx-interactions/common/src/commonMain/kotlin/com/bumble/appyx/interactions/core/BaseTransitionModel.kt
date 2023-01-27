package com.bumble.appyx.interactions.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlin.coroutines.EmptyCoroutineContext
import com.bumble.appyx.interactions.Logger
import com.bumble.appyx.interactions.core.Operation.Mode.*
import com.bumble.appyx.interactions.core.TransitionModel.Output

@SuppressWarnings("UnusedPrivateMember")
abstract class BaseTransitionModel<NavTarget, ModelState>(
    protected val scope: CoroutineScope = CoroutineScope(EmptyCoroutineContext + Dispatchers.Unconfined)
) : TransitionModel<NavTarget, ModelState> {
    abstract val initialState: ModelState

    abstract fun ModelState.destroyedElements(): Set<NavElement<NavTarget>>

    abstract fun ModelState.availableElements(): Set<NavElement<NavTarget>>

    fun availableElements(): Set<NavElement<NavTarget>> =
        state.value.targetState.availableElements()

    /**
     * A state queue that can be prefetched. Could also work as state history if we implement
     * reversing progress.
     */
    private val queue: MutableList<Output<ModelState>> by lazy {
        mutableListOf(
            Output.Update(
                index = 0,
                targetState = initialState
            )
        )
    }

    /**
     * 0..infinity
     */
    private var lastRecordedProgress: Float = 0f
    override val currentProgress: Float
        get() = lastRecordedProgress

    private val currentIndex: Int
        get() {
            /**
             *  Normally progress on any segment is a half-open interval: [0%, 100), so that
             *  100% is interpreted as 0% of the next segment.
             *  The only case when this won't work is when we reach the very end of possible progress,
             *  then there's no next segment to go to. Instead, let's interpret it as 100% of the last
             *  segment.
             */
            return (if (currentProgress == maxProgress) (currentProgress - 1) else currentProgress).toInt()
        }

    private val currentLocalProgress: Float
        get() = currentProgress - currentIndex

    /**
     * 0..infinity
     */
    override val maxProgress: Float
        get() = (queue.lastIndex + 1).toFloat()

    private val state: MutableStateFlow<Output<ModelState>> by lazy {
        MutableStateFlow(queue.first())
    }

    override val output: StateFlow<Output<ModelState>> by lazy {
        state
    }

    private var enforcedMode: Operation.Mode? = null

    override fun relaxExecutionMode() {
        enforcedMode = null
    }

    override fun operation(operation: Operation<ModelState>, overrideMode: Operation.Mode?): Boolean =
        when (enforcedMode ?: overrideMode ?: operation.mode) {
            IMMEDIATE -> {
                // Replacing in an active segment triggers IMMEDIATE mode as a side effect
                if (currentProgress > queue.lastIndex - 1) {
                    enforcedMode = IMMEDIATE
                }
                createUpdate(operation)
            }
            GEOMETRY -> {
                updateGeometry(operation)
            }
            KEYFRAME -> {
                createSegment(operation)
            }
        }.also {
            Logger.log(TAG, "Current progress: $currentProgress, queue: $queue")
        }

    private fun createUpdate(operation: Operation<ModelState>): Boolean {
        val baseLine = queue[currentIndex]

        return if (operation.isApplicable(baseLine.targetState)) {
            val transition = operation.invoke(baseLine.targetState)
            queue[currentIndex] = baseLine.deriveUpdate(transition)
            dropAfter(currentIndex) // An update clears the queue of unprocessed segments
            updateState()
            true
        } else {
            Logger.log(TAG, "Operation $operation is not applicable on state: $baseLine")
            false
        }
    }

    private fun updateGeometry(operation: Operation<ModelState>): Boolean {
        val remaining = queue.subList(currentIndex, queue.lastIndex)

        return if (remaining.all { operation.isApplicable(it.targetState) }) {
            // Replace the operation result into all the queued outputs
            for (i in currentIndex..queue.lastIndex) {
                val current = queue[i]
                val transition = operation.invoke(current.targetState)
                queue[i] = current.replace(transition.targetState)
            }
            updateState()
            true
        } else {
            Logger.log(TAG, "Operation $operation is not applicable on one or more queued states: $remaining")
            false
        }
    }

    private fun createSegment(operation: Operation<ModelState>): Boolean {
        val last = queue.last()
        val baselineState = last.targetState

        return if (operation.isApplicable(baselineState)) {
            val newSegment = operation.invoke(baselineState)
            val deriveSegment = last.deriveSegment(newSegment)
            when (queue.last()) {
                is Output.Segment -> queue.add(deriveSegment)
                is Output.Update -> queue[queue.lastIndex] = deriveSegment
            }
            updateState()
            true
        } else {
            Logger.log(TAG, "Operation $operation is not applicable on state: $baselineState")
            false
        }
    }

    override fun setProgress(progress: Float) {
        if (queue[currentIndex] !is Output.Segment<ModelState>) {
//            Logger.log(TAG, "Not in keyframe state, ignoring setProgress")
            return
        }

        // val progress = progress.coerceAtLeast(1f)
        Logger.log(TAG, "Progress update: $progress")
        if (progress.toInt() > lastRecordedProgress.toInt()) {
            // TODO uncomment when method is merged here
            //  com.bumble.appyx.interactions.core.navigation.BaseNavModel.onTransitionFinished
            // onTransitionFinished(state.value.fromState.map { it.key })
            Logger.log(TAG, "onTransitionFinished()")
        }

        lastRecordedProgress = progress
        updateState()
    }

    private fun updateState() {
        state.update { queue[currentIndex].withProgress(currentLocalProgress) }
    }

    override fun dropAfter(index: Int) {
        queue.dropLast(queue.lastIndex - index)
    }

    private companion object {
        private val TAG = BaseTransitionModel::class.java.name
    }
}
