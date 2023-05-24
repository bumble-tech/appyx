package com.bumble.appyx.components.spotlight.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.components.spotlight.Spotlight
import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.components.spotlight.SpotlightModel.State.ElementState.CREATED
import com.bumble.appyx.components.spotlight.SpotlightModel.State.ElementState.DESTROYED
import com.bumble.appyx.components.spotlight.SpotlightModel.State.ElementState.STANDARD
import com.bumble.appyx.components.spotlight.SpotlightModel.State.Position
import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.RawValue
import com.bumble.appyx.interactions.core.Element
import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.interactions.core.model.transition.BaseOperation
import com.bumble.appyx.interactions.core.model.transition.Operation
import kotlin.math.max

@Parcelize
// TODO cleanup SpotlightModel.State.positions if a position doesn't contain more elements
class UpdateElements<InteractionTarget : Any>(
    private val items: @RawValue List<InteractionTarget>,
    private val initialActiveIndex: Float? = null,
    override val mode: Operation.Mode = Operation.Mode.KEYFRAME
) : BaseOperation<SpotlightModel.State<InteractionTarget>>() {

    override fun isApplicable(state: SpotlightModel.State<InteractionTarget>): Boolean =
        true

    override fun createFromState(baseLineState: SpotlightModel.State<InteractionTarget>): SpotlightModel.State<InteractionTarget> {
        val positions = baseLineState.positions
        val newSize = max(positions.size, items.size)
        val newPositions = ArrayList<Position<InteractionTarget>>(newSize)
        for (i in 0 until newSize) {
            val elementForPosition =
                mutableMapOf<Element<InteractionTarget>, SpotlightModel.State.ElementState>()
            if (i < positions.size) {
                elementForPosition += positions[i].elements
            }
            if (i < items.size) {
                elementForPosition += (items[i].asElement() to CREATED)
            }
            newPositions.add(i, Position(elements = elementForPosition))
        }
        return baseLineState.copy(positions = newPositions)
    }

    override fun createTargetState(fromState: SpotlightModel.State<InteractionTarget>): SpotlightModel.State<InteractionTarget> =
        fromState.copy(
            positions = fromState.positions.map { position ->
                position.copy(
                    elements = position.elements.mapValues { (_, elementState) ->
                        when (elementState) {
                            CREATED -> STANDARD
                            STANDARD -> DESTROYED
                            DESTROYED -> DESTROYED
                        }
                    }
                )
            },
            activeIndex = initialActiveIndex ?: fromState.activeIndex
        )
}

fun <InteractionTarget : Any> Spotlight<InteractionTarget>.updateElements(
    items: List<InteractionTarget>,
    initialActiveIndex: Float? = null,
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec,
    mode: Operation.Mode = Operation.Mode.KEYFRAME
) {
    operation(
        operation = UpdateElements(
            items = items,
            initialActiveIndex = initialActiveIndex,
            mode = mode
        ),
        animationSpec = animationSpec,
    )
}
