package com.bumble.appyx.navigation.node.cakes.component.spotlighthero

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.bumble.appyx.interactions.core.model.BaseAppyxComponent
import com.bumble.appyx.interactions.core.ui.Visualisation
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.gesture.GestureFactory
import com.bumble.appyx.interactions.core.ui.gesture.GestureSettleConfig
import com.bumble.appyx.mapState
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.SpotlightHeroModel.State
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.visualisation.SpotlightHeroVisualisation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

open class SpotlightHero<InteractionTarget : Any>(
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
    private val model: SpotlightHeroModel<InteractionTarget>,
    visualisation: (UiContext) -> SpotlightHeroVisualisation<InteractionTarget>,
    gestureFactory: (TransitionBounds) -> GestureFactory<InteractionTarget, State<InteractionTarget>> = {
        GestureFactory.Noop()
    },
    animationSpec: AnimationSpec<Float> = spring(),
    gestureSettleConfig: GestureSettleConfig = GestureSettleConfig(
        completionThreshold = 0.2f,
        completeGestureSpec = animationSpec,
        revertGestureSpec = animationSpec,
    ),
    disableAnimations: Boolean = false,
    isDebug: Boolean = false
) : BaseAppyxComponent<InteractionTarget, State<InteractionTarget>>(
    scope = scope,
    model = model,
    visualisation = visualisation,
    gestureFactory = gestureFactory,
    defaultAnimationSpec = animationSpec,
    gestureSettleConfig = gestureSettleConfig,
    disableAnimations = disableAnimations,
    backPressStrategy = ExitHeroModeStrategy(scope),
    isDebug = isDebug
) {
    val currentState: State<InteractionTarget>
        get() = model.currentState

    val activeIndex: StateFlow<Float> = model.output
        .mapState(scope) { it.currentTargetState.activeIndex }

    val activeElement: StateFlow<InteractionTarget> = model.output
        .mapState(scope) { it.currentTargetState.activeElement }

    val mode: StateFlow<SpotlightHeroModel.Mode> = model.output
        .mapState(scope) { it.currentTargetState.mode }

    private var _heroProgress: MutableStateFlow<StateFlow<Float>> =
        MutableStateFlow(MutableStateFlow(0f))

    @Composable
    fun heroProgress(): Float =
        _heroProgress
            .collectAsState().value
            .collectAsState().value

    override fun onVisualisationReady(visualisation: Visualisation<InteractionTarget, State<InteractionTarget>>) {
        super.onVisualisationReady(visualisation)
        _heroProgress.update {
            (visualisation as SpotlightHeroVisualisation).heroProgress.renderValueFlow
        }
    }
}
