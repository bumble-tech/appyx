package com.bumble.appyx.navmodel2.spotlight.operation

import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.core.navigation2.NavElement
import com.bumble.appyx.core.navigation2.Operation.Noop
import com.bumble.appyx.core.navigation2.NavElements
import com.bumble.appyx.core.navigation2.NavTransition
import com.bumble.appyx.core.navigation2.Operation
import com.bumble.appyx.navmodel2.spotlight.Spotlight
import com.bumble.appyx.navmodel2.spotlight.Spotlight.State
import com.bumble.appyx.navmodel2.spotlight.Spotlight.State.ACTIVE
import com.bumble.appyx.navmodel2.spotlight.Spotlight.State.INACTIVE_AFTER
import com.bumble.appyx.navmodel2.spotlight.Spotlight.State.INACTIVE_BEFORE
import com.bumble.appyx.navmodel2.spotlight.SpotlightElements
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
class UpdateElements<NavTarget : Any>(
    private val elements: @RawValue List<NavTarget>,
    private val initialActiveIndex: Int? = null,
) : Operation<NavTarget, State> {

    override fun isApplicable(elements: NavElements<NavTarget, State>) = true

    override fun invoke(elements: NavElements<NavTarget, State>): NavTransition<NavTarget, State> {
        if (initialActiveIndex != null) {
            require(initialActiveIndex in this.elements.indices) {
                "Initial active index $initialActiveIndex is out of bounds of provided list of items: ${this.elements.indices}"
            }
        }
        val state = if (initialActiveIndex == null) {
            val currentActiveElement = elements.find { it.state == ACTIVE }

            // if current navTarget exists in the new list of items and initialActiveIndex is null
            // then keep existing navTarget active
            if (this.elements.contains(currentActiveElement?.key?.navTarget)) {
                this.elements.toSpotlightElements(elements.indexOf(currentActiveElement))
            } else {
                // if current navTarget does not exist in the new list of items and initialActiveIndex is null
                // then set 0 as active index
                this.elements.toSpotlightElements(0)
            }
        } else {
            this.elements.toSpotlightElements(initialActiveIndex)
        }
        
        return NavTransition(
            fromState = elements,
            targetState = state
        )
    }
}

// FIXME this needs the InputSource
//fun <T : Any> Spotlight<T>.updateElements(
//    items: List<T>,
//    initialActiveItem: Int? = null
//) {
//    enqueue(UpdateElements(items, initialActiveItem))
//}

internal fun <NavTarget> List<NavTarget>.toSpotlightElements(activeIndex: Int): NavElements<NavTarget, State> =
    mapIndexed { index, item ->
        val state = when {
            index < activeIndex -> INACTIVE_BEFORE
            index == activeIndex -> ACTIVE
            else -> INACTIVE_AFTER
        }

        return@mapIndexed NavElement(
            key = NavKey(item),
            fromState = state,
            targetState = state,
            operation = Noop()
        )
    }
