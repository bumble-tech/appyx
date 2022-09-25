package com.bumble.appyx.sandbox.client.spotlightadvancedexample

import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import com.bumble.appyx.samples.common.profile.Profile
import com.bumble.appyx.samples.common.profile.ProfileCard
import com.bumble.appyx.samples.common.profile.ProfileCardNode
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.node
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced
import com.bumble.appyx.navmodel.spotlightadvanced.backpresshandler.GoToPrevious
import com.bumble.appyx.navmodel.spotlightadvanced.isCarousel
import com.bumble.appyx.navmodel.spotlightadvanced.operation.next
import com.bumble.appyx.navmodel.spotlightadvanced.operation.previous
import com.bumble.appyx.navmodel.spotlightadvanced.operation.switchToCarousel
import com.bumble.appyx.navmodel.spotlightadvanced.operation.switchToPager
import com.bumble.appyx.navmodel.spotlightadvanced.transitionhandler.rememberSpotlightAdvancedSlider
import kotlinx.parcelize.Parcelize

@Suppress("MaxLineLength")
class SpotlightAdvancedExampleNode(
    buildContext: BuildContext,
    private val spotlightAdvanced: SpotlightAdvanced<NavTarget> = SpotlightAdvanced(
        items = Profile.allProfiles
            .shuffled()
            .take(listOf(5, 7, 9, 11).random())
            .map { NavTarget.ProfileCard(it) },
        savedStateMap = buildContext.savedStateMap,
        backPressHandler = GoToPrevious(),
    )
) : ParentNode<SpotlightAdvancedExampleNode.NavTarget>(
    buildContext = buildContext,
    navModel = spotlightAdvanced
) {

    sealed class NavTarget : Parcelable {
        @Parcelize
        class ProfileCard(val profile: Profile) : NavTarget()
    }


    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
        when (navTarget) {
            is NavTarget.ProfileCard -> node(buildContext) { ProfileCard(navTarget.profile) }
        }


    @Composable
    override fun View(modifier: Modifier) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(color = Color.Black)
        ) {
            Children(
                modifier = Modifier.fillMaxWidth(),
                transitionHandler = rememberSpotlightAdvancedSlider(),
                navModel = spotlightAdvanced
            )
            Row(
                modifier = Modifier
                    .wrapContentSize()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val isCarousel by spotlightAdvanced.isCarousel().collectAsState(initial = false)
                TextButton(
                    onClick = {
                        if (isCarousel) {
                            spotlightAdvanced.switchToPager()
                        } else {
                            spotlightAdvanced.switchToCarousel()
                        }
                    }
                ) {
                    Text(
                        text = (if (isCarousel) "Pager" else "Carousel").toUpperCase(Locale.current),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                TextButton(
                    onClick = { spotlightAdvanced.previous() }
                ) {
                    Text(
                        color = Color.White,
                        text = "Previous".toUpperCase(Locale.current),
                        fontWeight = FontWeight.Bold
                    )
                }
                TextButton(
                    onClick = { spotlightAdvanced.next() }
                ) {
                    Text(
                        color = Color.White,
                        text = "Next".toUpperCase(Locale.current),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
