package com.bumble.appyx.components.stable.backstack

import com.bumble.appyx.interactions.core.Element

val <T : Any> BackStackModel<T>.activeElement: Element<T>
    get() = output.value.currentTargetState.active

val <T : Any> BackStackModel<T>.activeInteractionTarget: T
    get() = activeElement.interactionTarget
