package com.bumble.appyx.sandbox.client.sharedelement.with_pager

import android.os.Parcelable
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.navigation.transition.sharedElement
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.node
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.push
import com.bumble.appyx.navmodel.backstack.transitionhandler.rememberBackstackFader
import com.bumble.appyx.samples.common.profile.Profile
import com.bumble.appyx.samples.common.profile.ProfileImage
import kotlinx.parcelize.Parcelize

class SharedElementPagerParentNode(
    buildContext: BuildContext,
    private val backStack: BackStack<NavTarget> = BackStack(
        savedStateMap = buildContext.savedStateMap,
        initialElement = NavTarget.Pager
    )
) : ParentNode<SharedElementPagerParentNode.NavTarget>(
    navModel = backStack,
    buildContext = buildContext
) {

    sealed class NavTarget : Parcelable {
        @Parcelize
        data object Pager : NavTarget()

        @Parcelize
        data class Detail(val index: Int) : NavTarget()
    }

    @OptIn(ExperimentalSharedTransitionApi::class)
    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node {
        return when (navTarget) {
            is NavTarget.Pager -> node(buildContext) {
                Box {
                    val pagerState = rememberPagerState { Profile.allProfiles.size }

                    HorizontalPager(
                        state = pagerState,
                        pageSize = PageSize.Fill,
                        contentPadding = PaddingValues(horizontal = 10.dp),
                        flingBehavior = PagerDefaults.flingBehavior(
                            state = pagerState,
                            snapAnimationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy)
                        )
                    ) { pageIndex ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    backStack.push(NavTarget.Detail(pageIndex))
                                }
                        ) {
                            val profile = Profile.allProfiles[pageIndex]

                            ProfileImage(
                                profile.drawableRes, modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .sharedElement(key = "$pageIndex image")
                            )

                            Text(
                                text = "${profile.name}, ${profile.age}",
                                color = Color.Black,
                                fontSize = 30.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .sharedElement(key = "$pageIndex text")
                                    .padding(16.dp)
                            )
                        }

                    }

                }
            }

            is NavTarget.Detail -> node(buildContext) {
                val pageIndex = navTarget.index
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    val profile = Profile.allProfiles[pageIndex]

                    ProfileImage(
                        profile.drawableRes, modifier = Modifier
                            .fillMaxSize()
                            .sharedElement(key = "$pageIndex image")
                    )

                    Text(
                        text = "${profile.name}, ${profile.age}",
                        color = Color.White,
                        fontSize = 30.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .sharedElement(key = "$pageIndex text")
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    )
                }

            }
        }
    }

    @Composable
    override fun View(modifier: Modifier) {
        Children(
            navModel = backStack,
            withSharedElementTransition = true,
            transitionHandler = rememberBackstackFader(transitionSpec = { tween(300) })
        )
    }
}