package com.bumble.appyx.navigation.component.spotlighthero


import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.interactions.core.model.backpresshandlerstrategies.BaseBackPressHandlerStrategy
import com.bumble.appyx.mapState
import com.bumble.appyx.navigation.component.spotlighthero.SpotlightHeroModel.Mode.HERO
import com.bumble.appyx.navigation.component.spotlighthero.SpotlightHeroModel.Mode.LIST
import com.bumble.appyx.navigation.component.spotlighthero.operation.SetHeroMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

class ExitHeroModeStrategy<NavTarget : Any>(
    val scope: CoroutineScope,
    val animationSpec: AnimationSpec<Float>? = null
) : BaseBackPressHandlerStrategy<NavTarget, SpotlightHeroModel.State<NavTarget>>() {

    override val canHandleBackPress: StateFlow<Boolean> by lazy {
        transitionModel.output.mapState(scope) { output ->
            output.currentTargetState.mode == HERO
        }
    }

    override fun handleBackPress(): Boolean {
        appyxComponent.operation(operation = SetHeroMode(LIST), animationSpec)

        return true
    }
}

