package com.bumble.appyx.navigation.node.datingcards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumble.appyx.components.experimental.cards.Cards
import com.bumble.appyx.components.experimental.cards.CardsModel
import com.bumble.appyx.components.experimental.cards.ui.CardsMotionController
import com.bumble.appyx.navigation.composable.AppyxComponent
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.datingcards.DatingCardsNode.InteractionTarget
import com.bumble.appyx.navigation.node.profilecard.ProfileCardNode
import com.bumble.appyx.navigation.ui.appyx_dark
import com.bumble.appyx.samples.common.profile.Profile
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize

class DatingCardsNode(
    buildContext: BuildContext,
    private val cards: Cards<InteractionTarget> =
        Cards(
            model = CardsModel(
                initialItems = Profile.allProfiles.shuffled().map {
                    InteractionTarget.ProfileCard(it)
                },
                savedStateMap = buildContext.savedStateMap
            ),
            motionController = { CardsMotionController(it) },
            gestureFactory = { CardsMotionController.Gestures(it) },
        )

) : ParentNode<InteractionTarget>(
    buildContext = buildContext,
    appyxComponent = cards
) {

    sealed class InteractionTarget : Parcelable {
        @Parcelize
        class ProfileCard(val profile: Profile) : InteractionTarget()
    }

    override fun resolve(interactionTarget: InteractionTarget, buildContext: BuildContext): Node =
        ProfileCardNode(buildContext, (interactionTarget as InteractionTarget.ProfileCard).profile)

    @Composable
    override fun View(modifier: Modifier) {
        AppyxComponent(
            modifier = modifier
                .fillMaxSize()
                .background(appyx_dark)
                .padding(16.dp),
            appyxComponent = cards,
        )
    }
}
