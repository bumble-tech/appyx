package com.bumble.appyx.navigation.node.datingcards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumble.appyx.components.experimental.cards.Cards
import com.bumble.appyx.components.experimental.cards.CardsModel
import com.bumble.appyx.components.experimental.cards.ui.CardsVisualisation
import com.bumble.appyx.navigation.composable.AppyxNavigationComponent
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.datingcards.DatingCardsNode.NavTarget
import com.bumble.appyx.navigation.node.profilecard.ProfileCardNode
import com.bumble.appyx.navigation.ui.appyx_dark
import com.bumble.appyx.samples.common.profile.Profile
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize

class DatingCardsNode(
    buildContext: BuildContext,
    private val cards: Cards<NavTarget> =
        Cards(
            model = CardsModel(
                initialItems = Profile.allProfiles.shuffled().map {
                    NavTarget.ProfileCard(it)
                },
                savedStateMap = buildContext.savedStateMap
            ),
            visualisation = { CardsVisualisation(it) },
            gestureFactory = { CardsVisualisation.Gestures(it) },
        )

) : ParentNode<NavTarget>(
    buildContext = buildContext,
    appyxComponent = cards
) {

    sealed class NavTarget : Parcelable {
        @Parcelize
        class ProfileCard(val profile: Profile) : NavTarget()
    }

    override fun buildChildNode(navTarget: NavTarget, buildContext: BuildContext): Node =
        ProfileCardNode(buildContext, (navTarget as NavTarget.ProfileCard).profile)

    @Composable
    override fun View(modifier: Modifier) {
        AppyxNavigationComponent(
            modifier = modifier
                .fillMaxSize()
                .background(appyx_dark)
                .padding(16.dp),
            appyxComponent = cards,
        )
    }
}
