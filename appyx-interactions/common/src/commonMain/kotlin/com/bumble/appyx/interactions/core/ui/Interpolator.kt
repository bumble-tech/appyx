package com.bumble.appyx.interactions.core.ui

import androidx.compose.animation.core.SpringSpec
import com.bumble.appyx.interactions.core.Keyframes
import com.bumble.appyx.interactions.core.NavElement
import com.bumble.appyx.interactions.core.Segment
import com.bumble.appyx.interactions.core.TransitionModel
import com.bumble.appyx.interactions.core.Update
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

interface Interpolator<NavTarget, ModelState> {

    val clipToBounds: Boolean
        get() = false

    val finishedAnimations: Flow<NavElement<NavTarget>>

    fun overrideAnimationSpec(springSpec: SpringSpec<Float>) {
        // TODO remove default once all implementations have been migrated to BaseInterpolator
    }

    fun isAnimating(): StateFlow<Boolean> = MutableStateFlow(false)

    fun map(
        output: TransitionModel.Output<ModelState>
    ): Flow<List<FrameModel<NavTarget>>> =
        mapCore(output)

    fun mapCore(
        output: TransitionModel.Output<ModelState>
    ): Flow<List<FrameModel<NavTarget>>> =
        when (output) {
            is Keyframes -> {
                //Produce new frame model every time we switch segments
                output.currentIndexFlow.distinctUntilChanged().map { mapKeyframes(output) }
            }
            is Update -> MutableStateFlow(mapUpdate(output))
        }

    fun mapOutput(
        output: TransitionModel.Output<ModelState>
    ) = when (output) {
        is Keyframes -> mapKeyframes(output)
        is Update -> mapUpdate(output)
    }

    fun mapKeyframes(
        keyframes: Keyframes<ModelState>
    ): List<FrameModel<NavTarget>> =
        mapSegment(
            keyframes.currentSegment,
            keyframes.segmentProgress
        )

    fun mapSegment(
        segment: Segment<ModelState>,
        segmentProgress: StateFlow<Float>
    ): List<FrameModel<NavTarget>>

    fun mapUpdate(
        update: Update<ModelState>
    ): List<FrameModel<NavTarget>>
}
