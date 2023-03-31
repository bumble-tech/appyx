package com.bumble.appyx.transitionmodel.permanent

import com.bumble.appyx.interactions.core.Element
import com.bumble.appyx.transitionmodel.backstack.BackStackModel

val <T : Any> BackStackModel<T>.activeElement: Element<T>
    get() = output.value.currentTargetState.active

val <T : Any> BackStackModel<T>.activeInteractionTarget: T
    get() = activeElement.interactionTarget
