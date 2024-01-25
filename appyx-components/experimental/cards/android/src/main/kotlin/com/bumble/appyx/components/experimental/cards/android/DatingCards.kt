package com.bumble.appyx.components.experimental.cards.android

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.bumble.appyx.components.experimental.cards.Cards
import com.bumble.appyx.components.experimental.cards.CardsModel
import com.bumble.appyx.components.experimental.cards.ui.CardsVisualisation
import com.bumble.appyx.demos.common.profile.Profile
import com.bumble.appyx.demos.common.profile.ProfileCard
import com.bumble.appyx.interactions.core.AppyxInteractionsContainer
import com.bumble.appyx.interactions.gesture.GestureValidator.Companion.permissiveValidator
import com.bumble.appyx.interactions.core.ui.helper.AppyxComponentSetup
import com.bumble.appyx.interactions.theme.appyx_dark
import kotlin.math.roundToInt

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
            visualisation = { CardsVisualisation(it) },
            gestureFactory = { CardsVisualisation.Gestures(it) },
            animateSettle = true
        )
    }

    AppyxComponentSetup(cards)

    AppyxInteractionsContainer(
        modifier = modifier
            .fillMaxSize()
            .background(appyx_dark)
            .padding(16.dp),
        screenWidthPx = (LocalConfiguration.current.screenWidthDp * LocalDensity.current.density).roundToInt(),
        screenHeightPx = (LocalConfiguration.current.screenHeightDp * LocalDensity.current.density).roundToInt(),
        appyxComponent = cards,
        gestureValidator = permissiveValidator,
    ) { elementUiModel ->
        ProfileCard(
            profile = elementUiModel.element.interactionTarget.profile,
            modifier = Modifier.fillMaxSize().then(elementUiModel.modifier)
        )
    }
}

private sealed class DatingCardsInteractionTarget {
    class ProfileCard(val profile: Profile) : DatingCardsInteractionTarget()
}
