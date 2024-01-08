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
import com.bumble.appyx.interactions.core.ui.math.lerpFloat
import com.bumble.appyx.navigation.composable.AppyxNavigationComponent
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.cakes.CakeListNode.NavTarget
import com.bumble.appyx.navigation.component.spotlighthero.SpotlightHero
import com.bumble.appyx.navigation.component.spotlighthero.SpotlightHeroModel
import com.bumble.appyx.navigation.component.spotlighthero.SpotlightHeroModel.Mode.HERO
import com.bumble.appyx.navigation.component.spotlighthero.SpotlightHeroModel.Mode.LIST
import com.bumble.appyx.navigation.component.spotlighthero.operation.activate
import com.bumble.appyx.navigation.component.spotlighthero.operation.setHeroMode
import com.bumble.appyx.navigation.component.spotlighthero.operation.toggleHeroMode
import com.bumble.appyx.navigation.component.spotlighthero.visualisation.SpotlightHeroGestures
import com.bumble.appyx.navigation.component.spotlighthero.visualisation.default.SpotlightHeroDefaultVisualisation
import com.bumble.appyx.navigation.node.cart.Cart
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize
import kotlinx.coroutines.delay
import kotlin.math.abs

private val animationSpec = spring<Float>(stiffness = Spring.StiffnessLow)

class CakeListNode(
    buildContext: BuildContext,
    private val cart: Cart,
    private val model: SpotlightHeroModel<NavTarget> = SpotlightHeroModel(
        items = cakes.map { NavTarget.Backdrop(it) to NavTarget.CakeImage(it) },
        initialActiveIndex = 0f,
        savedStateMap = buildContext.savedStateMap
    ),
    private val spotlight: SpotlightHero<NavTarget> =
        SpotlightHero(
            model = model,
            animationSpec = animationSpec,
            visualisation = { SpotlightHeroDefaultVisualisation(it, model.currentState) },
            gestureFactory = { SpotlightHeroGestures(it) }
        ),
) : ParentNode<NavTarget>(
    buildContext = buildContext,
    appyxComponent = spotlight
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
        spotlight.toggleHeroMode()
    }

    @Composable
    override fun View(modifier: Modifier) {
        val heroProgress = spotlight.heroProgress()
        val width = lerpFloat(0.6f, 1f, heroProgress)

        Box(
            modifier = modifier.fillMaxSize()
        ) {
            AppyxNavigationComponent(
                appyxComponent = spotlight,
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(width)
                    .fillMaxHeight(1f)
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
                    val currentTarget = spotlight.activeElement.collectAsState().value
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
        } while (abs(index - spotlight.activeIndex.value.toInt()) < 2)

        goToCake(cakes[index])
        delay(delay)
    }

    suspend fun goToCake(cake: Cake): CakeListNode = executeAction {
        val index = cakes.indexOf(cake).toFloat()
        spotlight.activate(index)
    }

    suspend fun leaveHeroMode(delay: Long = 0): CakeListNode = executeAction {
        spotlight.setHeroMode(LIST)
        delay(delay)
    }

    suspend fun enterHeroMode(delay: Long = 0): CakeListNode = executeAction {
        spotlight.setHeroMode(HERO)
        delay(delay)
    }
}
