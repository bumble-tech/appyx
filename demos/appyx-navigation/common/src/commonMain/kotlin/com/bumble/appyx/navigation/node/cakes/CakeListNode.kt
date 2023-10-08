package com.bumble.appyx.navigation.node.cakes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumble.appyx.components.spotlight.Spotlight
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.SpotlightHeroModel
import com.bumble.appyx.components.spotlight.ui.slider.SpotlightSlider
import com.bumble.appyx.navigation.composable.AppyxComponent
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.cakes.CakeListNode.NavTarget
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.SpotlightHero
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.visualisation.SpotlightHeroGestures
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.visualisation.backdrop.SpotlightHeroBackdropVisualisation
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.visualisation.main.SpotlightHeroMainVisualisation
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize

class CakeListNode(
    buildContext: BuildContext,
    private val spotlight: SpotlightHero<NavTarget> =
        SpotlightHero(
            model = SpotlightHeroModel(
                items = List(7) { NavTarget.CakeDetails },
                initialActiveIndex = 0f,
                savedStateMap = buildContext.savedStateMap
            ),
            visualisation = { SpotlightHeroMainVisualisation(it) },
             gestureFactory = { SpotlightHeroGestures(it) }
        )
) : ParentNode<NavTarget>(
    buildContext = buildContext,
    appyxComponent = spotlight
) {
    sealed class NavTarget : Parcelable {
        @Parcelize
        object CakeDetails : NavTarget()
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
        when (navTarget) {
            is NavTarget.CakeDetails -> CakeDetailsNode(buildContext)
        }

    @Composable
    override fun View(modifier: Modifier) {
        Box(modifier = modifier.fillMaxSize()) {
            AppyxComponent(
                appyxComponent = spotlight,
                modifier = Modifier
                    .fillMaxSize(0.6f)
                    .padding(top = 64.dp)
                    .align(Alignment.Center)
            )
        }
    }
}
