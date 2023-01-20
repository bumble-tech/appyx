package com.bumble.appyx.appyxnavigation.node.datingcards

import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumble.appyx.appyxnavigation.node.profilecard.ProfileCardNode
import com.bumble.appyx.appyxnavigation.ui.appyx_dark
import com.bumble.appyx.interactions.core.ui.GestureSpec
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
    private val cardsModel: CardsModel<NavTarget> = CardsModel(
        initialItems = Profile.allProfiles.shuffled().map {
            NavTarget.ProfileCard(it)
        }
    )
) : ParentNode<DatingCardsNode.NavTarget>(
    buildContext = buildContext,
    transitionModel = cardsModel
) {

    sealed class NavTarget : Parcelable {
        @Parcelize
        class ProfileCard(val profile: Profile) : NavTarget()
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
        ProfileCardNode(buildContext, (navTarget as NavTarget.ProfileCard).profile)

    @Composable
    override fun View(modifier: Modifier) {
        val coroutineScope = rememberCoroutineScope()
        val cards = remember {
            Cards(
                scope = coroutineScope,
                model = cardsModel,
                interpolator = { CardsProps(it) },
                gestureFactory = { CardsProps.Gestures(it) },
            )
        }

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
