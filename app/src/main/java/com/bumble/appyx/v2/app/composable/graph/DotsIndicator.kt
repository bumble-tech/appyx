package com.bumble.appyx.v2.app.composable.graph

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bumble.appyx.v2.app.ui.appyx_yellow1
import com.bumble.appyx.v2.core.routing.source.spotlight.Spotlight
import com.bumble.appyx.v2.core.routing.source.spotlight.activeIndex
import com.bumble.appyx.v2.core.routing.source.spotlight.elementsCount
import com.bumble.appyx.v2.core.routing.transition.TransitionSpec
import kotlin.math.min

@Composable
fun DotsIndicator(
    totalDots: Int,
    selectedIndex: Int,
    selectedColor: Color = appyx_yellow1,
    unSelectedColor: Color = Color.LightGray,
    selectedSize: Dp = 9.dp,
    unSelectedSize: Dp = 6.dp,
    maxDots: Int = 3,
    dotTransitionSpecs: TransitionSpec<MutableState<Boolean>, Dp> = {
        spring(
            visibilityThreshold = Dp.VisibilityThreshold,
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
    }
) {
    LazyRow(
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.spacedBy(unSelectedSize, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items(min(maxDots, totalDots)) { index ->
            val isSelected = remember(index, selectedIndex) {
                mutableStateOf(index == selectedIndex)
            }
            val transition = updateTransition(isSelected)
            val color = transition.animateColor { isSelected ->
                when (isSelected.value) {
                    true -> selectedColor
                    false -> unSelectedColor
                }
            }
            val size = transition.animateDp(
                transitionSpec = dotTransitionSpecs
            ) { isSelected ->
                when (isSelected.value) {
                    true -> selectedSize
                    false -> unSelectedSize
                }
            }

            Box(
                modifier = Modifier
                    .size(size.value)
                    .clip(CircleShape)
                    .background(color.value)
            )
        }
    }
}

@Composable
fun SpotlightDotsIndicator(
    spotlight: Spotlight<*>,
    selectedColor: Color = appyx_yellow1,
    unSelectedColor: Color = Color.DarkGray,
    selectedSize: Dp = 9.dp,
    unSelectedSize: Dp = 6.dp,
    dotTransitionSpecs: TransitionSpec<MutableState<Boolean>, Dp> = {
        spring(
            visibilityThreshold = Dp.VisibilityThreshold,
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
    }
) {
    val selectedItemState = spotlight.activeIndex().collectAsState(initial = 0)

    DotsIndicator(
        totalDots = spotlight.elementsCount(),
        selectedIndex = selectedItemState.value,
        selectedColor = selectedColor,
        unSelectedColor = unSelectedColor,
        selectedSize = selectedSize,
        unSelectedSize = unSelectedSize,
        dotTransitionSpecs = dotTransitionSpecs
    )
}
