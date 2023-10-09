package com.bumble.appyx.navigation.node.cakes

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.bumble.appyx.interactions.core.model.plus
import com.bumble.appyx.navigation.composable.AppyxComponent
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.cakes.CakeListNode.NavTarget
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.SpotlightHero
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.SpotlightHeroModel
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.SpotlightHeroModel.Mode.HERO
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.SpotlightHeroModel.Mode.LIST
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.operation.toggleHeroMode
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.visualisation.SpotlightHeroGestures
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.visualisation.backdrop.SpotlightHeroBackdropVisualisation
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.visualisation.main.SpotlightHeroMainVisualisation
import com.bumble.appyx.navigation.node.cakes.model.Cake
import com.bumble.appyx.navigation.node.cakes.model.cakes
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize

private val animationSpec = spring<Float>(stiffness = Spring.StiffnessLow)

class CakeListNode(
    buildContext: BuildContext,
    private val spotlightBackDrop: SpotlightHero<NavTarget> =
        SpotlightHero(
            model = SpotlightHeroModel(
                items = cakes.map { NavTarget.Backdrop(it) },
                initialActiveIndex = 0f,
                savedStateMap = buildContext.savedStateMap
            ),
            animationSpec = animationSpec,
            visualisation = { SpotlightHeroBackdropVisualisation(it) },
            gestureFactory = { SpotlightHeroGestures(it) }
        ),
    private val spotlightMain: SpotlightHero<NavTarget> =
        SpotlightHero(
            model = SpotlightHeroModel(
                items = cakes.map { NavTarget.CakeImage(it) },
                initialActiveIndex = 0f,
                savedStateMap = buildContext.savedStateMap
            ),
            animationSpec = animationSpec,
            visualisation = { SpotlightHeroMainVisualisation(it) },
            gestureFactory = { SpotlightHeroGestures(it) }
        )
) : ParentNode<NavTarget>(
    buildContext = buildContext,
    appyxComponent = spotlightBackDrop + spotlightMain
) {

    sealed class NavTarget : Parcelable {
        @Parcelize
        data class Backdrop(
            val cake: Cake
        ) : NavTarget()

        @Parcelize
        data class CakeImage(
            val cake: Cake
        ) : NavTarget()
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
        when (navTarget) {
            is NavTarget.Backdrop -> CakeBackdropNode(buildContext, navTarget.cake) {
                toggleHeroMode()
            }
            is NavTarget.CakeImage -> CakeImageNode(buildContext, navTarget.cake) {
                toggleHeroMode()
            }
        }

    private fun toggleHeroMode() {
        spotlightBackDrop.toggleHeroMode()
        spotlightMain.toggleHeroMode()
    }

    @Composable
    override fun View(modifier: Modifier) {
        val mode = spotlightBackDrop.mode.collectAsState(LIST)
        val width by animateFloatAsState(
            targetValue = when (mode.value) {
                LIST -> 0.6f
                HERO -> 1f
            },
            animationSpec = animationSpec
        )

        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AppyxComponent(
                appyxComponent = spotlightBackDrop,
                draggables = listOf(spotlightBackDrop, spotlightMain),
                modifier = Modifier
                    .fillMaxWidth(width)
                    .fillMaxHeight(1f)
                    .background(Color.DarkGray)
            )
            AppyxComponent(
                appyxComponent = spotlightMain,
                draggables = listOf(spotlightBackDrop, spotlightMain),
                modifier = Modifier
                    .fillMaxWidth(width)
                    .fillMaxHeight(1f)
            )
        }
    }
}
