package com.bumble.appyx.core.navigation.model.permanent.operation

import android.os.Parcelable
import com.bumble.appyx.core.navigation.EmptyState
import com.bumble.appyx.core.navigation.NavElement
import com.bumble.appyx.core.navigation.NavElements
import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.core.navigation.model.permanent.PermanentNavModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Add<T : Parcelable>(
    private val key: NavKey<T>
) : PermanentOperation<T> {

    override fun isApplicable(elements: NavElements<T, EmptyState>): Boolean =
        !elements.any { it.key == key }

    override fun invoke(
        elements: NavElements<T, EmptyState>
    ): NavElements<T, EmptyState> =
        if (elements.any { it.key == key }) {
            elements
        } else {
            elements + NavElement(
                key = key,
                fromState = EmptyState.INSTANCE,
                targetState = EmptyState.INSTANCE,
                operation = this,
            )
        }
}

fun <T : Parcelable> PermanentNavModel<T>.add(navKey: NavKey<T>) {
    accept(Add(navKey))
}
