package com.bumble.appyx.navigation.component.spotlighthero

import com.bumble.appyx.interactions.core.Element
import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.interactions.core.model.transition.BaseTransitionModel
import com.bumble.appyx.navigation.component.spotlighthero.SpotlightHeroModel.State
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize
import com.bumble.appyx.utils.multiplatform.RawValue
import com.bumble.appyx.utils.multiplatform.SavedStateMap

class SpotlightHeroModel<NavTarget : Any>(
    items: List<Pair<NavTarget, NavTarget>>,
    initialMode: Mode = Mode.LIST,
    initialActiveIndex: Float = 0f,
    savedStateMap: SavedStateMap?,
) : BaseTransitionModel<NavTarget, State<NavTarget>>(
    savedStateMap = savedStateMap
) {
    enum class Mode {
        LIST, HERO
    }

    @Parcelize
    data class State<NavTarget>(
        val mode: Mode = Mode.LIST,
        val positions: @RawValue List<Position<NavTarget>>,
        val activeIndex: Float
    ) : Parcelable {

        @Parcelize
        data class Position<NavTarget>(
            val backdrop: Element<NavTarget>,
            val main: Element<NavTarget>,
        ) : Parcelable

        fun hasPrevious(): Boolean =
            activeIndex >= 1

        fun hasNext(): Boolean =
            activeIndex <= positions.lastIndex - 1

        val activeElement: NavTarget =
            positions[activeIndex.toInt()].main.interactionTarget
    }

    override val initialState: State<NavTarget> =
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

    val currentState: State<NavTarget>
        get() = output.value.currentTargetState

    override fun State<NavTarget>.removeDestroyedElement(
        element: Element<NavTarget>
    ): State<NavTarget> =
        this

    override fun State<NavTarget>.removeDestroyedElements(): State<NavTarget> =
        this

    override fun State<NavTarget>.availableElements(): Set<Element<NavTarget>> =
        positions
            .flatMap { listOf(it.backdrop, it.main) }
            .toSet()

    override fun State<NavTarget>.destroyedElements(): Set<Element<NavTarget>> =
        emptySet()
}
