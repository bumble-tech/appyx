package com.bumble.appyx.interactions.core.ui

import androidx.compose.ui.Modifier
import com.bumble.appyx.interactions.core.NavElement

data class FrameModel<NavTarget>(
    val navElement: NavElement<NavTarget>,
    val modifier: Modifier,
    val progress: Float,
    val state: State = State.UNSPECIFIED
) {
    enum class State  {
        VISIBLE, PARTIALLY_VISIBLE, INVISIBLE, UNSPECIFIED
    }
}
