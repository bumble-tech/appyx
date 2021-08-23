package com.github.zsoltk.composeribs.core.routing.impl.backstack

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Transition
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset

typealias BackStackTransitionSpec = @Composable Transition.Segment<BackStack.TransitionState>.() -> FiniteAnimationSpec<Offset>
