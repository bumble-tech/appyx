package com.bumble.appyx.interactions.core.ui

import com.bumble.appyx.interactions.core.NavElement

data class MatchedProps<InteractionTarget, Props: BaseProps>(
    val element: NavElement<InteractionTarget>,
    val props: Props
)
