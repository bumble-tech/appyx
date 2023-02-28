package com.bumble.appyx.interactions.core

import com.bumble.appyx.interactions.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

data class Keyframes<ModelState>(
    val queue: List<Segment<ModelState>>,
    val initialProgress: Float = 0f
) : TransitionModel.Output<ModelState>() {

    val currentIndex: Int
        get() {
            /**
             *  Normally progress on any segment is a half-open interval: [0%, 100), so that
             *  100% is interpreted as 0% of the next segment.
             *  The only case when this won't work is when we reach the very end of possible progress,
             *  then there's no next segment to go to. Instead, let's interpret it as 100% of the last
             *  segment.
             */
            return (if (reachedMaxProgress) (this.progress - 1) else this.progress).toInt()
        }

    val maxProgress: Float
        get() = (queue.lastIndex + 1).toFloat()

    val reachedMaxProgress: Boolean
        get() = progress == maxProgress

    val currentSegment: Segment<ModelState>
        get() = queue[currentIndex]

    val progressFlow = MutableStateFlow(initialProgress)

    val currentIndexFlow = progressFlow.map { progress ->
        (if (progress == maxProgress) (progress - 1) else progress).toInt()
    }.distinctUntilChanged()

    val currentSegmentFlow = currentIndexFlow.map {
        queue[it]
    }
    val currentSegmentTargetStateFlow = currentIndexFlow.map {
        queue[it].targetState
    }

    fun getSegmentProgress(segmentIndex: Int): Flow<Float> =
        progressFlow.map { it.toSegmentProgress(segmentIndex) }.filterNotNull()

    val progress: Float
        get() = progressFlow.value

    override val currentTargetState: ModelState
        get() = currentSegment.targetState

    override val lastTargetState: ModelState
        get() = queue.last().targetState

    override fun replace(targetState: ModelState): TransitionModel.Output<ModelState> =
        copy(
            queue = queue.map {
                it.replace(targetState)
            }
        )

    override fun deriveKeyframes(navTransition: NavTransition<ModelState>): Keyframes<ModelState> =
        copy(
            queue = queue + listOf(Segment(navTransition)),
            initialProgress = progress
        )

    override fun deriveUpdate(navTransition: NavTransition<ModelState>): Update<ModelState> =
        Update(
            currentTargetState = navTransition.targetState
        )

    fun dropAfter(index: Int): Keyframes<ModelState> =
        if (index < queue.lastIndex) {
            copy(
                queue = queue.dropLast(queue.lastIndex - index)
            )
        } else this

    fun setProgress(progress: Float, onTransitionFinished: (ModelState) -> Unit) {
        val currentProgress = this.progress.toInt()
        val progress = progress.coerceIn(0f, maxProgress)
        Logger.log("Keyframes", "$progress")
        progressFlow.value = progress
        if (progress.toInt() > currentProgress) {
            Logger.log("Keyframes", "onTransitionFinished()")
            onTransitionFinished(currentSegment.fromState)
        }
    }
}

internal fun Float.toSegmentProgress(segmentIndex: Int): Float? {
    val segmentProgress = this - segmentIndex
    return if (segmentProgress in 0f..1f) {
        segmentProgress
    } else null
}

