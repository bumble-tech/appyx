package com.bumble.appyx.app.node.cards

import android.os.Parcelable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.coroutineScope
import com.bumble.appyx.app.node.cards.CardsExampleNode.NavTarget
import com.bumble.appyx.samples.common.profile.Profile
import com.bumble.appyx.samples.common.profile.ProfileCardNode
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.navmodel.cards.Cards
import com.bumble.appyx.navmodel.cards.operation.indicateLike
import com.bumble.appyx.navmodel.cards.operation.indicatePass
import com.bumble.appyx.navmodel.cards.operation.voteLike
import com.bumble.appyx.navmodel.cards.operation.votePass
import com.bumble.appyx.navmodel.cards.transitionhandler.rememberCardsTransitionHandler
import kotlinx.coroutines.delay
import kotlinx.parcelize.Parcelize

class CardsExampleNode(
    buildContext: BuildContext,
    private val cards: Cards<NavTarget> = Cards(
        initialItems = Profile.allProfiles.shuffled().map {
            NavTarget.ProfileCard(it)
        }
    ),
) : ParentNode<NavTarget>(
    buildContext = buildContext,
    navModel = cards
) {

    init {
        lifecycle.coroutineScope.launchWhenStarted {
            repeat(cards.elements.value.size / 4 - 1) {
                delay(1500)
                cards.indicateLike()
                delay(1000)
                cards.indicatePass()
                delay(1000)
                cards.votePass()
                delay(1000)
                cards.voteLike()
                delay(500)
                cards.voteLike()
                delay(500)
                cards.voteLike()
            }
        }
    }

    sealed class NavTarget : Parcelable {
        @Parcelize
        class ProfileCard(val profile: Profile) : NavTarget()
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
        when (navTarget) {
            is NavTarget.ProfileCard -> ProfileCardNode(buildContext, navTarget.profile)
        }

    @Composable
    override fun View(modifier: Modifier) {
        Children(
            modifier = modifier
                .fillMaxSize()
                .padding(20.dp),
            navModel = cards,
            transitionHandler = rememberCardsTransitionHandler()
        ) {
            children<NavTarget> { child ->
                child(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

