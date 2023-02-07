package com.bumble.appyx.interactions.core

import com.bumble.appyx.interactions.Logger
data class Keyframes<ModelState>(
    val queue: List<Segment<ModelState>>,
    val progress: Float = 0f,
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

    val segmentProgress: Float
        get() = this.progress - currentIndex

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
            queue = queue + listOf(Segment(navTransition))
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


    fun setProgress(progress: Float, onTransitionFinished: (ModelState) -> Unit): Keyframes<ModelState> {
        Logger.log("Keyframes", "Progress update: $progress")
        if (progress.toInt() > this.progress.toInt()) {
            Logger.log("Keyframes", "onTransitionFinished()")
            onTransitionFinished(currentSegment.fromState)
        }

        return copy(
            progress = progress.coerceIn(0f, maxProgress)
        )
    }
}

