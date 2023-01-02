package com.bumble.appyx.core.navigation.model.permanent.operation

import android.os.Parcelable
import com.bumble.appyx.core.navigation.EmptyState
import com.bumble.appyx.core.navigation.NavElement
import com.bumble.appyx.core.navigation.NavElements
import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.core.navigation.model.permanent.PermanentNavModel
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/**
 * Adds [NavTarget] into [PermanentNavModel] only if it is not there yet.
 */
@Parcelize
data class AddUnique<NavTarget : Parcelable>(
    private val navTarget: NavTarget,
) : PermanentOperation<NavTarget> {

    override fun isApplicable(elements: NavElements<NavTarget, EmptyState>): Boolean =
        !elements.any { it.key.navTarget == navTarget }

    override fun invoke(
        elements: NavElements<NavTarget, EmptyState>
    ): NavElements<NavTarget, EmptyState> =
        if (elements.any { it.key.navTarget == navTarget }) {
            elements
        } else {
            elements + NavElement(
                key = NavKey(navTarget),
                fromState = EmptyState.INSTANCE,
                targetState = EmptyState.INSTANCE,
                operation = this,
            )
        }
}

fun <NavTarget : Parcelable> PermanentNavModel<NavTarget>.addUnique(navTarget: NavTarget) {
    accept(AddUnique(navTarget))
}
