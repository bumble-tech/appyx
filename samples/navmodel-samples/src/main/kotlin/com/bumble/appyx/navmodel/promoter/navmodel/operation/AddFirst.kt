package com.bumble.appyx.navmodel.promoter.navmodel.operation

import android.os.Parcelable
import com.bumble.appyx.navmodel.promoter.navmodel.Promoter
import com.bumble.appyx.navmodel.promoter.navmodel.Promoter.State.CREATED
import com.bumble.appyx.navmodel.promoter.navmodel.PromoterElement
import com.bumble.appyx.navmodel.promoter.navmodel.PromoterElements
import com.bumble.appyx.core.navigation.NavElements
import com.bumble.appyx.core.navigation.NavKey
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class AddFirst<T : Parcelable>(
    private val element: T
) : PromoterOperation<T> {

    override fun isApplicable(elements: PromoterElements<T>): Boolean =
        true

    override fun invoke(
        elements: PromoterElements<T>,
    ): NavElements<T, Promoter.State> {
        val new = PromoterElement(
            key = NavKey(element),
            fromState = CREATED,
            targetState = CREATED,
            operation = this
        )

        return listOf(new) + elements
    }
}

fun <T : Parcelable> Promoter<T>.addFirst(element: T) {
    accept(AddFirst(element))
}
