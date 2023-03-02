package com.bumble.appyx.interactions.core.ui.output

import com.bumble.appyx.interactions.core.Element

data class MatchedProps<InteractionTarget, Props: BaseProps>(
    val element: Element<InteractionTarget>,
    val props: Props
)
