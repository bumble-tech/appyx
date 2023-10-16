package com.bumble.appyx.navigation.node.cakes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.core.model.plus
import com.bumble.appyx.interactions.core.ui.math.lerpFloat
import com.bumble.appyx.navigation.composable.AppyxComponent
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.cakes.CakeListNode.NavTarget
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.SpotlightHero
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.SpotlightHeroModel
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.SpotlightHeroModel.Mode.HERO
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.SpotlightHeroModel.Mode.LIST
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.operation.activate
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.operation.setHeroMode
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.operation.toggleHeroMode
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.visualisation.SpotlightHeroGestures
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.visualisation.backdrop.SpotlightHeroBackdropVisualisation
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.visualisation.main.SpotlightHeroMainVisualisation
import com.bumble.appyx.navigation.node.cakes.model.Cake
import com.bumble.appyx.navigation.node.cakes.model.Cart
import com.bumble.appyx.navigation.node.cakes.model.cakes
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize
import kotlinx.coroutines.delay
import kotlin.math.abs

private val animationSpec = spring<Float>(stiffness = Spring.StiffnessLow)

class CakeListNode(
    buildContext: BuildContext,
    private val cart: Cart,
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
        abstract val cake: Cake

        @Parcelize
        data class Backdrop(
            override val cake: Cake
        ) : NavTarget()

        @Parcelize
        data class CakeImage(
            override val cake: Cake
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
        val heroProgress = spotlightMain.heroProgress()
        val width = lerpFloat(0.6f, 1f, heroProgress)

        Box(
            modifier = modifier.fillMaxSize()
        ) {
            AppyxComponent(
                appyxComponent = spotlightBackDrop,
                draggables = listOf(spotlightBackDrop, spotlightMain),
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(width)
                    .fillMaxHeight(1f)
            )
            AppyxComponent(
                appyxComponent = spotlightMain,
                draggables = listOf(spotlightBackDrop, spotlightMain),
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(width)
                    .fillMaxHeight(1f),
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                AnimatedVisibility(
                    visible = heroProgress > 0.9f,
                    enter = fadeIn() + slideIn { IntOffset(x = 0, y = 20) },
                    exit = fadeOut() + slideOut { IntOffset(x = 0, y = 20) },
                ) {
                    val currentTarget = spotlightMain.activeElement.collectAsState().value
                    val selectedCake = currentTarget.cake
                    CakeDetailsSheet(
                        cake = selectedCake,
                        addToCartAction = { cart.add(selectedCake) }
                    )
                }
            }
        }
    }

    suspend fun goToRandomOtherCake(delay: Long = 0): CakeListNode = executeAction {
        var index: Int
        do {
            index = cakes.indices.random()
        } while (abs(index - spotlightMain.activeIndex.value.toInt()) < 2)

        goToCake(cakes[index])
        delay(delay)
    }

    suspend fun goToCake(cake: Cake): CakeListNode = executeAction {
        val index = cakes.indexOf(cake).toFloat()
        spotlightMain.activate(index)
        spotlightBackDrop.activate(index)
    }

    suspend fun leaveHeroMode(delay: Long = 0): CakeListNode = executeAction {
        spotlightMain.setHeroMode(LIST)
        spotlightBackDrop.setHeroMode(LIST)
        delay(delay)
    }

    suspend fun enterHeroMode(delay: Long = 0): CakeListNode = executeAction {
        spotlightMain.setHeroMode(HERO)
        spotlightBackDrop.setHeroMode(HERO)
        delay(delay)
    }
}
