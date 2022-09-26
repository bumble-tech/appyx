package com.bumble.appyx.navmodel.spotlightadvanced.operation

import com.bumble.appyx.core.navigation.NavElements
import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.core.navigation.Operation
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.State
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.State.Active
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.State.InactiveAfter
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.State.InactiveBefore
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvancedElement
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvancedElements
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
class UpdateElements<T : Any>(
    private val elements: @RawValue List<T>,
    private val initialActiveIndex: Int? = null,
) : SpotlightAdvancedOperation<T> {

    override fun isApplicable(elements: NavElements<T, State>) = true

    override fun invoke(elements: NavElements<T, State>): NavElements<T, State> {
        if (initialActiveIndex != null) {
            require(initialActiveIndex in this.elements.indices) {
                "Initial active index $initialActiveIndex is out of bounds of provided list of items:" +
                        " ${this.elements.indices}"
            }
        }
        return if (initialActiveIndex == null) {
            val currentActiveElement = elements.find { it.targetState == Active }

            // if current navTarget exists in the new list of items and initialActiveIndex is null
            // then keep existing navTarget active
            if (this.elements.contains(currentActiveElement?.key?.navTarget)) {
                this.elements.toSpotlightAdvancedElements(elements.indexOf(currentActiveElement))
            } else {
                // if current navTarget does not exist in the new list of items and initialActiveIndex is null
                // then set 0 as active index
                this.elements.toSpotlightAdvancedElements(0)
            }
        } else {
            this.elements.toSpotlightAdvancedElements(initialActiveIndex)
        }
    }
}

fun <T : Any> SpotlightAdvanced<T>.updateElements(
    items: List<T>,
    initialActiveItem: Int? = null
) {
    accept(UpdateElements(items, initialActiveItem))
}

internal fun <T> List<T>.toSpotlightAdvancedElements(activeIndex: Int): SpotlightAdvancedElements<T> =
    mapIndexed { index, item ->
        val state = when {
            index < activeIndex -> InactiveBefore
            index == activeIndex -> Active
            else -> InactiveAfter
        }
        SpotlightAdvancedElement(
            key = NavKey(item),
            fromState = state,
            targetState = state,
            operation = Operation.Noop()
        )
    }
