package com.bumble.appyx.components.backstack

import com.bumble.appyx.interactions.model.Element

val <T : Any> BackStackModel<T>.activeElement: Element<T>
    get() = output.value.currentTargetState.active

val <T : Any> BackStackModel<T>.activeInteractionTarget: T
    get() = activeElement.interactionTarget
