package com.bumble.appyx.navmodel.backstack.backpresshandler

import android.os.Parcelable
import com.bumble.appyx.core.navigation.backpresshandlerstrategies.BaseBackPressHandlerStrategy
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.BackStackElements
import com.bumble.appyx.navmodel.backstack.operation.Pop
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PopBackPressHandler<NavTarget : Parcelable> :
    BaseBackPressHandlerStrategy<NavTarget, BackStack.State>() {

    override val canHandleBackPressFlow: Flow<Boolean> by lazy {
        navModel.elements.map(::areThereStashedElements)
    }

    private fun areThereStashedElements(elements: BackStackElements<NavTarget>) =
        elements.any { it.targetState == BackStack.State.STASHED }

    override fun onBackPressed() {
        navModel.accept(Pop())
    }
}
