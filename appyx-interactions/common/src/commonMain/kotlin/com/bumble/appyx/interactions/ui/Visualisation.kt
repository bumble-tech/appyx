package com.bumble.appyx.interactions.ui

import androidx.compose.animation.core.SpringSpec
import com.bumble.appyx.interactions.model.transition.Keyframes
import com.bumble.appyx.interactions.model.Element
import com.bumble.appyx.interactions.model.transition.Segment
import com.bumble.appyx.interactions.model.transition.TransitionModel
import com.bumble.appyx.interactions.model.transition.Update
import com.bumble.appyx.interactions.model.transition.toSegmentProgress
import com.bumble.appyx.interactions.ui.context.TransitionBoundsAware
import com.bumble.appyx.interactions.ui.output.ElementUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

interface Visualisation<InteractionTarget, ModelState> : TransitionBoundsAware {

    val finishedAnimations: Flow<Element<InteractionTarget>>

    fun overrideAnimationSpec(springSpec: SpringSpec<Float>) {
        // TODO remove default once all implementations have been migrated to BaseMotionController
    }

    fun isAnimating(): StateFlow<Boolean> = MutableStateFlow(false)

    fun map(
        output: TransitionModel.Output<ModelState>
    ): Flow<List<ElementUiModel<InteractionTarget>>> =
        when (output) {
            is Keyframes -> {
                //Produce new frame model every time we switch segments
                output.currentIndexFlow.map { mapKeyframes(output, it) }
            }
            is Update -> MutableStateFlow(mapUpdate(output))
        }

    fun mapKeyframes(
        keyframes: Keyframes<ModelState>,
        segmentIndex: Int
    ): List<ElementUiModel<InteractionTarget>> =
        mapSegment(
            keyframes.currentSegment,
            keyframes.getSegmentProgress(segmentIndex),
            checkNotNull(keyframes.progress.toSegmentProgress(segmentIndex)) {
                "Segment progress should be in bounds"
            }
        )

    fun mapSegment(
        segment: Segment<ModelState>,
        segmentProgress: Flow<Float>,
        initialProgress: Float
    ): List<ElementUiModel<InteractionTarget>>

    fun mapUpdate(
        update: Update<ModelState>
    ): List<ElementUiModel<InteractionTarget>>
}
