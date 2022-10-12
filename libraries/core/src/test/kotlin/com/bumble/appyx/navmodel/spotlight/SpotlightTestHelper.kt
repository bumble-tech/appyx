package com.bumble.appyx.navmodel.spotlight

import com.bumble.appyx.core.navigation.Operation
import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.navmodel.spotlight.Spotlight.State
import com.bumble.appyx.navmodel.spotlight.operation.NavTarget

internal fun <T : NavTarget> spotlightElement(
    element: T,
    key: NavKey<T> = NavKey(navTarget = element),
    fromState: State,
    targetState: State,
    operation: Operation<T, State> = Operation.Noop()
) = SpotlightElement(
    key = key,
    fromState = fromState,
    targetState = targetState,
    operation = operation
)
