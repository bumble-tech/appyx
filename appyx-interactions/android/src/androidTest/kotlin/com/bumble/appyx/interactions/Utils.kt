package com.bumble.appyx.interactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.bumble.appyx.interactions.core.InteractionModel
import com.bumble.appyx.interactions.core.ui.InteractionModelSetup
import com.bumble.appyx.interactions.sample.Children
import com.bumble.appyx.interactions.theme.appyx_dark

fun <InteractionTarget : Any, ModelState : Any> ComposeContentTestRule.setupInteractionModel(
    interactionModel: InteractionModel<InteractionTarget, ModelState>
) {
    setContent {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = appyx_dark
        ) {
            InteractionModelSetup(interactionModel)
            TestChildrenUi(
                interactionModel = interactionModel
            )
        }
    }
}


@Composable
private fun <NavTarget : Any, ModelState : Any> TestChildrenUi(
    interactionModel: InteractionModel<NavTarget, ModelState>
) {
    Children(
        modifier = Modifier
            .fillMaxSize(),
        interactionModel = interactionModel,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black)
        ) {
            Text(
                text = "${it.element.interactionTarget}",
                modifier = Modifier.align(Alignment.Center),
                color = Color.White
            )
        }
    }
}
