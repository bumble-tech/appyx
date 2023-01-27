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
        get() = output.value.let {
            when (it) {
                is Output.Segment -> it.index + it.progress
                is Output.Update -> it.index
            }.toFloat()
        }

    val output: StateFlow<Output<ModelState>>

    sealed class Output<ModelState> {
        abstract val index: Int
        abstract val targetState: ModelState

        abstract fun withProgress(progress: Float): Output<ModelState>

        abstract fun replace(targetState: ModelState): Output<ModelState>

        abstract fun deriveSegment(navTransition: NavTransition<ModelState>): Segment<ModelState>

        abstract fun deriveUpdate(navTransition: NavTransition<ModelState>): Update<ModelState>

        data class Segment<ModelState>(
            override val index: Int,
            val navTransition: NavTransition<ModelState>,
            val progress: Float,
        ) : Output<ModelState>() {

            override val targetState: ModelState
                get() = navTransition.targetState

            override fun withProgress(progress: Float): Output<ModelState> =
                copy(progress = progress)

            override fun replace(targetState: ModelState): Output<ModelState> =
                copy(
                    navTransition = navTransition.copy(
                        targetState =  targetState
                    )
                )
            override fun deriveUpdate(navTransition: NavTransition<ModelState>): Update<ModelState> =
                Update(
                    index = index,
                    history = listOf(
                        this.navTransition.fromState,
                        this.navTransition.targetState,
                        navTransition.fromState
                    ),
                    targetState = targetState
                )

            override fun deriveSegment(navTransition: NavTransition<ModelState>): Segment<ModelState> =
                Segment(
                    index = index + 1,
                    navTransition = navTransition,
                    progress = 0f,
                )
        }

        data class Update<ModelState>(
            override val index: Int,
            val history: List<ModelState> = emptyList(),
            override val targetState: ModelState,
        ) : Output<ModelState>() {

            override fun withProgress(progress: Float): Output<ModelState> =
                this

            override fun replace(targetState: ModelState): Output<ModelState> =
                Update(
                    index = index,
                    history = history,
                    targetState = targetState
                )
            override fun deriveUpdate(navTransition: NavTransition<ModelState>): Update<ModelState> =
                Update(
                    index = index,
                    history = history + listOf(
//                        this.targetState,
//                        navTransition.fromState
                    ),
                    targetState = navTransition.targetState
                )

            override fun deriveSegment(navTransition: NavTransition<ModelState>): Segment<ModelState> =
                Segment(
                    index = index + 1,
                    navTransition = navTransition,
                    progress = 0f,
                )
        }
    }

    fun relaxExecutionMode()

    fun operation(
        operation: Operation<ModelState>,
        overrideMode: Operation.Mode? = null
    ): Boolean

    fun setProgress(progress: Float)

    fun dropAfter(segmentIndex: Int)
}
