package com.bumble.appyx.demos.experimental.datingcards

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.bumble.appyx.components.experimental.cards.Cards
import com.bumble.appyx.components.experimental.cards.CardsModel
import com.bumble.appyx.components.experimental.cards.operation.like
import com.bumble.appyx.components.experimental.cards.operation.pass
import com.bumble.appyx.components.experimental.cards.ui.CardsMotionController
import com.bumble.appyx.demos.common.AppyxWebSample
import com.bumble.appyx.demos.common.ChildSize
import com.bumble.appyx.demos.common.InteractionTarget

@Composable
fun DatingCardsSample(
    screenWidthPx: Int,
    screenHeightPx: Int,
    modifier: Modifier = Modifier,
) {
    val cards = remember {
        Cards(
            model = CardsModel(
                initialItems = List(15) { InteractionTarget.Element(it) },
                savedStateMap = null
            ),
            motionController = { CardsMotionController(it) },
            gestureFactory = { CardsMotionController.Gestures(it) },
            animateSettle = true
        )
    }

    val animationSpec: AnimationSpec<Float> = spring(stiffness = Spring.StiffnessVeryLow * 2)
    val actions = mapOf(
        "Pass" to { cards.pass(animationSpec = animationSpec) },
        "Like" to { cards.like(animationSpec = animationSpec) },
    )
    AppyxWebSample(
        screenWidthPx = screenWidthPx,
        screenHeightPx = screenHeightPx,
        appyxComponent = cards,
        actions = actions,
        childSize = ChildSize.MAX,
        modifier = modifier,
    )
}