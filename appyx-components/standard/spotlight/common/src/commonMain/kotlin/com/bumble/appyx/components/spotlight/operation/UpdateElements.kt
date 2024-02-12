package com.bumble.appyx.components.spotlight.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.components.spotlight.Spotlight
import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.components.spotlight.SpotlightModel.State.ElementState.CREATED
import com.bumble.appyx.components.spotlight.SpotlightModel.State.ElementState.DESTROYED
import com.bumble.appyx.components.spotlight.SpotlightModel.State.ElementState.STANDARD
import com.bumble.appyx.components.spotlight.SpotlightModel.State.Position
import com.bumble.appyx.interactions.model.Element
import com.bumble.appyx.interactions.model.asElement
import com.bumble.appyx.interactions.model.transition.BaseOperation
import com.bumble.appyx.interactions.model.transition.Operation
import com.bumble.appyx.utils.multiplatform.Parcelize
import com.bumble.appyx.utils.multiplatform.RawValue
import kotlin.math.max

@Parcelize
// TODO cleanup SpotlightModel.State.positions if a position doesn't contain more elements
class UpdateElements<NavTarget : Any>(
    private val items: @RawValue List<NavTarget>,
    private val initialActiveIndex: Float? = null,
    override var mode: Operation.Mode = Operation.Mode.KEYFRAME
) : BaseOperation<SpotlightModel.State<NavTarget>>() {

    override fun isApplicable(state: SpotlightModel.State<NavTarget>): Boolean =
        true

    override fun createFromState(
        baseLineState: SpotlightModel.State<NavTarget>
    ): SpotlightModel.State<NavTarget> {
        val positions = baseLineState.positions
        val newSize = max(positions.size, items.size)
        val newPositions = ArrayList<Position<NavTarget>>(newSize)
        for (i in 0 until newSize) {
            val elementsForPosition =
                mutableMapOf<Element<NavTarget>, SpotlightModel.State.ElementState>()
            if (i < positions.size) {
                elementsForPosition += positions[i].elements
            }
            if (i < items.size) {
                elementsForPosition += (items[i].asElement() to CREATED)
            }
            newPositions.add(i, Position(elements = elementsForPosition))
        }
        return baseLineState.copy(positions = newPositions)
    }

    override fun createTargetState(
        fromState: SpotlightModel.State<NavTarget>
    ): SpotlightModel.State<NavTarget> =
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

fun <NavTarget : Any> Spotlight<NavTarget>.updateElements(
    items: List<NavTarget>,
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
