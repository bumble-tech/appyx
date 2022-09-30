package com.bumble.appyx.core.portal

import android.os.Parcelable
import com.bumble.appyx.core.navigation.NavElements
import com.bumble.appyx.core.navigation.Operation
import com.bumble.appyx.core.navigation.backpresshandlerstrategies.BaseBackPressHandlerStrategy
import com.bumble.appyx.navmodel.backstack.BackStack
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.parcelize.Parcelize

class PortalClientNavModelPressHandler<NavTarget : Any> :
    BaseBackPressHandlerStrategy<NavTarget, BackStack.State>() {

    private val operation = Back<NavTarget>()

    override val canHandleBackPressFlow: Flow<Boolean> by lazy {
        navModel.elements.map(operation::isApplicable)
    }

    override fun onBackPressed() {
        navModel.accept(operation)
    }

    @Parcelize
    private class Back<NavTarget : Any> : Operation<NavTarget, BackStack.State>, Parcelable {

        override fun isApplicable(elements: NavElements<NavTarget, BackStack.State>): Boolean =
            elements.isNotEmpty()

        override fun invoke(elements: NavElements<NavTarget, BackStack.State>): NavElements<NavTarget, BackStack.State> =
            elements.take(elements.size - 1)

    }
}
