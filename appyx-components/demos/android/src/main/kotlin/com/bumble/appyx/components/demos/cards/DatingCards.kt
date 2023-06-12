package com.bumble.appyx.components.demos.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.bumble.appyx.components.demos.cards.ui.CardsMotionController
import com.bumble.appyx.interactions.core.DraggableChildren
import com.bumble.appyx.interactions.core.gesture.GestureValidator.Companion.permissiveValidator
import com.bumble.appyx.interactions.core.ui.helper.InteractionModelSetup
import com.bumble.appyx.interactions.theme.appyx_dark
import com.bumble.appyx.samples.common.profile.Profile
import com.bumble.appyx.samples.common.profile.ProfileCard
import kotlin.math.roundToInt

sealed class DatingCardsInteractionTarget {
    class ProfileCard(val profile: Profile) : DatingCardsInteractionTarget()
}

@Composable
fun DatingCards(modifier: Modifier = Modifier) {
    val cards = remember {
        Cards(
            model = CardsModel(
                initialItems = Profile.allProfiles.shuffled().map {
                    DatingCardsInteractionTarget.ProfileCard(it)
                },
                savedStateMap = null
            ),
            motionController = { CardsMotionController(it) },
            gestureFactory = { CardsMotionController.Gestures(it) },
            animateSettle = true
        )
    }

    InteractionModelSetup(cards)

    DraggableChildren(
        modifier = modifier
            .fillMaxSize()
            .background(appyx_dark)
            .padding(16.dp),
        screenWidthPx = (LocalConfiguration.current.screenWidthDp * LocalDensity.current.density).roundToInt(),
        screenHeightPx = (LocalConfiguration.current.screenHeightDp * LocalDensity.current.density).roundToInt(),
        interactionModel = cards,
        gestureValidator = permissiveValidator,
    ) { elementUiModel ->
        Box(
            modifier = modifier
                .then(elementUiModel.modifier)
                .then(modifier)
        ) {
            ProfileCard(profile = elementUiModel.element.interactionTarget.profile)
        }
    }
}
