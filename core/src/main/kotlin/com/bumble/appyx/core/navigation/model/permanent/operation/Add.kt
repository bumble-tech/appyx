package com.bumble.appyx.core.navigation.model.permanent.operation

import com.bumble.appyx.core.navigation.NavElement
import com.bumble.appyx.core.navigation.NavElements
import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.core.navigation.model.permanent.PermanentNavModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Add<T : Any>(
    private val key: NavKey<T>
) : PermanentOperation<T> {

    override fun isApplicable(elements: NavElements<T, Int>): Boolean =
        !elements.any { it.key == key }

    override fun invoke(
        elements: NavElements<T, Int>
    ): NavElements<T, Int> =
        if (elements.any { it.key == key }) {
            elements
        } else {
            elements + NavElement(
                key = key,
                fromState = 0,
                targetState = 0,
                operation = this,
            )
        }
}

fun <T : Any> PermanentNavModel<T>.add(navKey: NavKey<T>) {
    accept(Add(navKey))
}
