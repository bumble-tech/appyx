package com.bumble.appyx.demos.navigation.component.spotlighthero

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.bumble.appyx.demos.navigation.component.spotlighthero.SpotlightHeroModel.State
import com.bumble.appyx.demos.navigation.component.spotlighthero.visualisation.SpotlightHeroVisualisation
import com.bumble.appyx.interactions.core.model.BaseAppyxComponent
import com.bumble.appyx.interactions.core.ui.Visualisation
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.gesture.GestureFactory
import com.bumble.appyx.interactions.gesture.GestureSettleConfig
import com.bumble.appyx.mapState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

open class SpotlightHero<NavTarget : Any>(
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
    private val model: SpotlightHeroModel<NavTarget>,
    visualisation: (UiContext) -> SpotlightHeroVisualisation<NavTarget>,
    gestureFactory: (TransitionBounds) -> GestureFactory<NavTarget, State<NavTarget>> = {
        GestureFactory.Noop()
    },
    animationSpec: AnimationSpec<Float> = spring(),
    gestureSettleConfig: GestureSettleConfig = GestureSettleConfig(
        completionThreshold = 0.2f,
        completeGestureSpec = animationSpec,
        revertGestureSpec = animationSpec,
    ),
    disableAnimations: Boolean = false,
) : BaseAppyxComponent<NavTarget, State<NavTarget>>(
    scope = scope,
    model = model,
    visualisation = visualisation,
    gestureFactory = gestureFactory,
    defaultAnimationSpec = animationSpec,
    gestureSettleConfig = gestureSettleConfig,
    disableAnimations = disableAnimations,
    backPressStrategy = ExitHeroModeStrategy(scope)
) {
    val currentState: State<NavTarget>
        get() = model.currentState

    val activeIndex: StateFlow<Float> = model.output
        .mapState(scope) { it.currentTargetState.activeIndex }

    val activeElement: StateFlow<NavTarget> = model.output
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

    override fun onVisualisationReady(visualisation: Visualisation<NavTarget, State<NavTarget>>) {
        super.onVisualisationReady(visualisation)
        _heroProgress.update {
            (visualisation as SpotlightHeroVisualisation).heroProgress.renderValueFlow
        }
    }
}
