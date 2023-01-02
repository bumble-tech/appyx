package com.bumble.appyx.navmodel.promoter.navmodel.operation

import android.os.Parcelable
import com.bumble.appyx.navmodel.promoter.navmodel.Promoter
import com.bumble.appyx.navmodel.promoter.navmodel.PromoterElements
import com.bumble.appyx.core.navigation.NavElements
import kotlinx.parcelize.Parcelize

@Parcelize
class PromoteAll<T : Parcelable> : PromoterOperation<T> {

    override fun isApplicable(elements: PromoterElements<T>): Boolean =
        true

    override fun invoke(
        elements: PromoterElements<T>,
    ): NavElements<T, Promoter.State> =
        elements.transitionTo { it.targetState.next() }
}

fun <T : Parcelable> Promoter<T>.promoteAll() {
    accept(PromoteAll())
}
