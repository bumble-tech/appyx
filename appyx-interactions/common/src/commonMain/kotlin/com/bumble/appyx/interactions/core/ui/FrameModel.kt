package com.bumble.appyx.interactions.core.ui

import androidx.compose.ui.Modifier
import com.bumble.appyx.interactions.core.NavElement
import com.bumble.appyx.transitionmodel.backstack.BackstackFader

data class FrameModel<NavTarget>(
    val navElement: NavElement<NavTarget>,
    val modifier: Modifier,
    val progress: Float,
    val props: BackstackFader.Props? = null
)
