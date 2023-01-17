package com.bumble.appyx.interactions.sample

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.core.inputsource.DragProgressInputSource
import com.bumble.appyx.interactions.core.ui.RenderParams
import com.bumble.appyx.interactions.theme.appyx_dark
import com.bumble.appyx.samples.common.profile.Profile
import com.bumble.appyx.samples.common.profile.ProfileCard
import com.bumble.appyx.transitionmodel.cards.Cards
import com.bumble.appyx.transitionmodel.cards.interpolator.CardsProps
import kotlinx.coroutines.flow.map

sealed class DatingCardsNavTarget {
    class ProfileCard(val profile: Profile) : DatingCardsNavTarget()
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DatingCards(modifier: Modifier = Modifier) {
    val cards = remember {
        Cards<DatingCardsNavTarget>(
            initialItems = Profile.allProfiles.shuffled().map {
                DatingCardsNavTarget.ProfileCard(it)
            }
        )
    }

    val coroutineScope = rememberCoroutineScope()
    val drag = remember { DragProgressInputSource(cards, coroutineScope) }

    var elementSize by remember { mutableStateOf(IntSize(0, 0)) }
    val transitionParams by createTransitionParams(elementSize)
    val uiProps =
        remember(transitionParams) { CardsProps<DatingCardsNavTarget>(transitionParams) }
    val render = remember(uiProps) { cards.segments.map { uiProps.map(it) } }

    val density = LocalDensity.current

    Children(
        modifier = modifier
            .fillMaxSize()
            .background(appyx_dark)
            .padding(16.dp),
        renderParams = render.collectAsState(listOf()),
        onElementSizeChanged = { elementSize = it },
        element = {
            ElementWrapper(
                renderParams = it,
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(it.navElement.key) {
                        detectDragGestures(
                            onDragStart = { position ->
                                if (drag.gestureFactory == null) {
                                    drag.gestureFactory = { dragAmount ->
                                        uiProps.createGesture(position, dragAmount, density)
                                    }
                                }
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                drag.addDeltaProgress(dragAmount)
                            },
                            onDragEnd = {
                                drag.gestureFactory = null
                                drag.settle(
                                    roundUpThreshold = 0.15f,
                                    roundUpAnimationSpec = spring(
                                        stiffness = Spring.StiffnessLow
                                    ),
                                    roundDownAnimationSpec = spring(
                                        stiffness = Spring.StiffnessMedium
                                    )
                                )
                            }
                        )
                    }
            )
        }
    )
}

@Composable
fun ElementWrapper(
    renderParams: RenderParams<DatingCardsNavTarget, Cards.State>,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
            .then(renderParams.modifier)
            .then(modifier)
    ) {
        ProfileCard(profile = (renderParams.navElement.key.navTarget as DatingCardsNavTarget.ProfileCard).profile)
    }
}
