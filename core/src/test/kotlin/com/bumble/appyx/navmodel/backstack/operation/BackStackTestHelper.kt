package com.bumble.appyx.navmodel.backstack.operation

import com.bumble.appyx.core.navigation.Operation
import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.BackStackElement

internal fun <T : NavTarget> backStackElement(
    element: T,
    key: NavKey<T> = NavKey(navTarget = element),
    fromState: BackStack.TransitionState,
    targetState: BackStack.TransitionState,
    operation: Operation<T, BackStack.TransitionState>
) = BackStackElement(
    key = key,
    fromState = fromState,
    targetState = targetState,
    operation = operation
)
