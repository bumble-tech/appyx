package com.bumble.appyx.components.demos.cards

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.bumble.appyx.components.demos.cards.ui.CardsMotionController
import com.bumble.appyx.interactions.core.ui.helper.InteractionModelSetup
import com.bumble.appyx.interactions.core.ui.output.ElementUiModel
import com.bumble.appyx.interactions.sample.Children
import com.bumble.appyx.interactions.theme.appyx_dark
import com.bumble.appyx.samples.common.profile.Profile
import com.bumble.appyx.samples.common.profile.ProfileCard

sealed class DatingCardsNavTarget {
    class ProfileCard(val profile: Profile) : DatingCardsNavTarget()
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DatingCards(modifier: Modifier = Modifier) {
    val density = LocalDensity.current

    val coroutineScope = rememberCoroutineScope()
    val cards = remember {
        Cards(
            model = CardsModel(
                initialItems = Profile.allProfiles.shuffled().map {
                    DatingCardsNavTarget.ProfileCard(it)
                },
                savedStateMap = null
            ),
            motionController = { CardsMotionController(it)  },
            gestureFactory = { CardsMotionController.Gestures(it) },
            animateSettle = true
        )
    }

    InteractionModelSetup(cards)

    Children(
        modifier = modifier
            .fillMaxSize()
            .background(appyx_dark)
            .padding(16.dp),
        interactionModel = cards,
        element = {
            ElementWrapper(
                elementUiModel = it,
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(it.element.id) {
                        detectDragGestures(
                            onDragStart = { position -> cards.onStartDrag(position) },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                cards.onDrag(dragAmount, density)
                            },
                            onDragEnd = {
                                cards.onDragEnd(
                                    completionThreshold = 0.15f,
                                    completeGestureSpec = spring(stiffness = Spring.StiffnessLow),
                                    revertGestureSpec = spring(stiffness = Spring.StiffnessMedium)
                                )
                            }
                        )
                    }
            )
        }
    )
}

@Composable
fun ElementWrapper(
    elementUiModel: ElementUiModel<DatingCardsNavTarget.ProfileCard>,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
            .then(elementUiModel.modifier)
            .then(modifier)
    ) {
        ProfileCard(profile = elementUiModel.element.interactionTarget.profile)
    }
}
