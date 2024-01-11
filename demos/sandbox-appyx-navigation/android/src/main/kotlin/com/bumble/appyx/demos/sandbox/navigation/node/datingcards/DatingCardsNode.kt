package com.bumble.appyx.demos.sandbox.navigation.node.datingcards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumble.appyx.components.experimental.cards.Cards
import com.bumble.appyx.components.experimental.cards.CardsModel
import com.bumble.appyx.components.experimental.cards.ui.CardsVisualisation
import com.bumble.appyx.demos.common.profile.Profile
import com.bumble.appyx.demos.sandbox.navigation.node.datingcards.DatingCardsNode.NavTarget
import com.bumble.appyx.demos.sandbox.navigation.node.datingcards.DatingCardsNode.NavTarget.ProfileCard
import com.bumble.appyx.demos.sandbox.navigation.node.profilecard.ProfileCardNode
import com.bumble.appyx.demos.sandbox.navigation.ui.appyx_dark
import com.bumble.appyx.navigation.composable.AppyxNavigationContainer
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize

class DatingCardsNode(
    nodeContext: NodeContext,
    private val cards: Cards<NavTarget> =
        Cards(
            model = CardsModel(
                initialItems = Profile.allProfiles.shuffled().map {
                    NavTarget.ProfileCard(it)
                },
                savedStateMap = nodeContext.savedStateMap
            ),
            visualisation = { CardsVisualisation(it) },
            gestureFactory = { CardsVisualisation.Gestures(it) },
        )

) : ParentNode<NavTarget>(
    nodeContext = nodeContext,
    appyxComponent = cards
) {

    sealed class NavTarget : Parcelable {
        @Parcelize
        class ProfileCard(val profile: Profile) : NavTarget()
    }

    override fun buildChildNode(navTarget: NavTarget, nodeContext: NodeContext): Node =
        ProfileCardNode(nodeContext, (navTarget as ProfileCard).profile)

    @Composable
    override fun Content(modifier: Modifier) {
        AppyxNavigationContainer(
            modifier = modifier
                .fillMaxSize()
                .background(appyx_dark)
                .padding(16.dp),
            appyxComponent = cards,
        )
    }
}
