package com.bumble.appyx.interactions.core.ui

import androidx.compose.ui.Modifier
import com.bumble.appyx.interactions.core.NavElement
import kotlinx.coroutines.flow.StateFlow

data class FrameModel<NavTarget>(
    val navElement: NavElement<NavTarget>,
    val modifier: Modifier,
    val progress: StateFlow<Float>,
    val state: State = State.VISIBLE
) {
    enum class State  {
        VISIBLE, PARTIALLY_VISIBLE, INVISIBLE, UNSPECIFIED
    }
}

fun <NavTarget> List<FrameModel<NavTarget>>.toScreenState(): ScreenState<NavTarget> {
    val onScreen = mutableSetOf<NavElement<NavTarget>>()
    val offScreen = mutableSetOf<NavElement<NavTarget>>()
    forEach { frame ->
        if (frame.state == FrameModel.State.VISIBLE || frame.state == FrameModel.State.PARTIALLY_VISIBLE) {
            onScreen.add(frame.navElement)
        } else {
            offScreen.add(frame.navElement)
        }
    }

    return ScreenState(onScreen = onScreen, offScreen = offScreen)
}

