package com.bumble.appyx.navigation.node.cakes.component.spotlighthero

import com.bumble.appyx.interactions.core.Element
import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.interactions.core.model.transition.BaseTransitionModel
import com.bumble.appyx.interactions.core.state.SavedStateMap
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.SpotlightHeroModel.State
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize
import com.bumble.appyx.utils.multiplatform.RawValue

class SpotlightHeroModel<InteractionTarget : Any>(
    items: List<Pair<InteractionTarget, InteractionTarget>>,
    initialMode: Mode = Mode.LIST,
    initialActiveIndex: Float = 0f,
    savedStateMap: SavedStateMap?,
) : BaseTransitionModel<InteractionTarget, State<InteractionTarget>>(
    savedStateMap = savedStateMap
) {
    enum class Mode {
        LIST, HERO
    }

    @Parcelize
    data class State<InteractionTarget>(
        val mode: Mode = Mode.LIST,
        val positions: @RawValue List<Position<InteractionTarget>>,
        val activeIndex: Float
    ) : Parcelable {

        @Parcelize
        data class Position<InteractionTarget>(
            val backdrop: Element<InteractionTarget>,
            val main: Element<InteractionTarget>,
        ) : Parcelable

        fun hasPrevious(): Boolean =
            activeIndex >= 1

        fun hasNext(): Boolean =
            activeIndex <= positions.lastIndex - 1

        val activeElement: InteractionTarget =
            positions[activeIndex.toInt()].main.interactionTarget
    }

    override val initialState: State<InteractionTarget> =
        State(
            mode = initialMode,
            positions = items.map {
                State.Position(
                    backdrop = it.first.asElement(),
                    main = it.second.asElement(),
                )
            },
            activeIndex = initialActiveIndex
        )

    val currentState: State<InteractionTarget>
        get() = output.value.currentTargetState

    override fun State<InteractionTarget>.removeDestroyedElement(
        element: Element<InteractionTarget>
    ): State<InteractionTarget> =
        this

    override fun State<InteractionTarget>.removeDestroyedElements(): State<InteractionTarget> =
        this

    override fun State<InteractionTarget>.availableElements(): Set<Element<InteractionTarget>> =
        positions
            .flatMap { listOf(it.backdrop, it.main) }
            .toSet()

    override fun State<InteractionTarget>.destroyedElements(): Set<Element<InteractionTarget>> =
        emptySet()
}
