package com.bumble.appyx.core.portal

import com.bumble.appyx.core.navigation.BaseNavModel
import com.bumble.appyx.core.navigation.NavElements
import com.bumble.appyx.core.navigation.NavModelAdapter
import com.bumble.appyx.core.navigation.onscreen.OnScreenStateResolver
import com.bumble.appyx.core.navigation.onscreen.isOnScreen
import com.bumble.appyx.core.state.SavedStateMap
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.backpresshandler.PopBackPressHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PortalClientNavModel<NavTarget : Any>(
    savedStateMap: SavedStateMap?,
    private val inOnScreen: MutableStateFlow<Boolean> = MutableStateFlow(false),
    private val screenResolver: OnScreenStateResolver<BackStack.State> = OnScreenStateResolver { state ->
        if (!inOnScreen.value) false
        else when (state) {
            BackStack.State.CREATED,
            BackStack.State.STASHED,
            BackStack.State.DESTROYED -> false
            BackStack.State.ACTIVE -> true
        }
    },
) : BaseNavModel<NavTarget, BackStack.State>(
    savedStateMap = savedStateMap,
    finalState = BackStack.State.DESTROYED,
    screenResolver = screenResolver,
    backPressHandler = PortalClientNavModelPressHandler(),
    key = "PortalNavModel",
) {

    override val initialElements: NavElements<NavTarget, BackStack.State> = emptyList()

    override val screenState: StateFlow<NavModelAdapter.ScreenState<NavTarget, BackStack.State>> by lazy {
        // Recalculate screenState on every isOnScreen change
        inOnScreen
            .flatMapLatest {
                elements
                    .map { elements ->
                        NavModelAdapter.ScreenState(
                            onScreen = elements.filter { screenResolver.isOnScreen(it) },
                            offScreen = elements.filterNot { screenResolver.isOnScreen(it) },
                        )
                    }
            }
            .stateIn(scope, SharingStarted.Eagerly, NavModelAdapter.ScreenState())
    }

    init {
        // Trigger sanitizeOffScreenTransitions() when the property is changed
        scope.launch {
            inOnScreen.filterNot { it }.collect {
                updateState { it }
            }
        }
    }

    fun setVisibility(isOnScreen: Boolean) {
        this.inOnScreen.value = isOnScreen
    }

}