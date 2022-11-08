package com.bumble.appyx.core.navigation.model.permanent.operation

import com.bumble.appyx.core.navigation.NavElement
import com.bumble.appyx.core.navigation.NavElements
import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.core.navigation.model.permanent.PermanentNavModel
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class AddUnique<NavTarget : Any>(
    private val navTarget: @RawValue NavTarget,
) : PermanentOperation<NavTarget> {

    override fun isApplicable(elements: NavElements<NavTarget, Int>): Boolean =
        !elements.any { it.key.navTarget == navTarget }

    override fun invoke(
        elements: NavElements<NavTarget, Int>
    ): NavElements<NavTarget, Int> =
        if (elements.any { it.key.navTarget == navTarget }) {
            elements
        } else {
            elements + NavElement(
                key = NavKey(navTarget),
                fromState = 0,
                targetState = 0,
                operation = this,
            )
        }
}

fun <NavTarget : Any> PermanentNavModel<NavTarget>.addUnique(navTarget: NavTarget) {
    accept(AddUnique(navTarget))
}
