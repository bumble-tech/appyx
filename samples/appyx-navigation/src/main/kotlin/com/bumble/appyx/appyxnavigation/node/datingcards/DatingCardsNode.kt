package com.bumble.appyx.appyxnavigation.node.datingcards

import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumble.appyx.appyxnavigation.node.datingcards.DatingCardsNode.NavTarget
import com.bumble.appyx.appyxnavigation.node.profilecard.ProfileCardNode
import com.bumble.appyx.appyxnavigation.ui.appyx_dark
import com.bumble.appyx.interactions.core.ui.gesture.GestureSpec
import com.bumble.appyx.navigation.composable.Children
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.samples.common.profile.Profile
import com.bumble.appyx.transitionmodel.cards.Cards
import com.bumble.appyx.transitionmodel.cards.CardsModel
import com.bumble.appyx.transitionmodel.cards.interpolator.CardsProps
import kotlinx.parcelize.Parcelize

class DatingCardsNode(
    buildContext: BuildContext,
    private val cards: Cards<NavTarget> =
        Cards(
            model = CardsModel(
                initialItems = Profile.allProfiles.shuffled().map {
                    NavTarget.ProfileCard(it)
                }),
            interpolator = { CardsProps(it) },
            gestureFactory = { CardsProps.Gestures(it) },
        )

) : ParentNode<NavTarget>(
    buildContext = buildContext,
    interactionModel = cards
) {

    sealed class NavTarget : Parcelable {
        @Parcelize
        class ProfileCard(val profile: Profile) : NavTarget()
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
        ProfileCardNode(buildContext, (navTarget as NavTarget.ProfileCard).profile)

    @Composable
    override fun View(modifier: Modifier) {

        Children(
            modifier = modifier
                .fillMaxSize()
                .background(appyx_dark)
                .padding(16.dp),
            interactionModel = cards,
            gestureSpec = GestureSpec(completionThreshold = 0.15f)
        )
    }
}
