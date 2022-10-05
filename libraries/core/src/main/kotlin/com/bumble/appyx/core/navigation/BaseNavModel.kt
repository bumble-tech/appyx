package com.bumble.appyx.core.navigation

import androidx.activity.OnBackPressedCallback
import com.bumble.appyx.core.navigation.backpresshandlerstrategies.BackPressHandlerStrategy
import com.bumble.appyx.core.navigation.backpresshandlerstrategies.DontHandleBackPress
import com.bumble.appyx.core.navigation.onscreen.OnScreenStateResolver
import com.bumble.appyx.core.navigation.onscreen.isOnScreen
import com.bumble.appyx.core.navigation.operationstrategies.ExecuteImmediately
import com.bumble.appyx.core.navigation.operationstrategies.OperationStrategy
import com.bumble.appyx.core.plugin.BackPressHandler
import com.bumble.appyx.core.plugin.Destroyable
import com.bumble.appyx.core.state.MutableSavedStateMap
import com.bumble.appyx.core.state.SavedStateMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Base class for NavModel implementations.
 * Use this one if the base behaviour suffice your requirements, you want to use backPressHandler or operationStrategy,
 * and to reduce boilerplate.
 *
 * If more granular configuration is required, you should inherit NavModel interface instead.
 */
abstract class BaseNavModel<NavTarget, State>(
    private val backPressHandler: BackPressHandlerStrategy<NavTarget, State> = DontHandleBackPress(),
    private val operationStrategy: OperationStrategy<NavTarget, State> = ExecuteImmediately(),
    private val screenResolver: OnScreenStateResolver<State>,
    initialElements: NavElements<NavTarget, State>,
    protected val scope: CoroutineScope = CoroutineScope(EmptyCoroutineContext + Dispatchers.Unconfined),
    private val finalStates: Set<State>,
    private val key: String = KEY_NAV_MODEL,
    savedStateMap: SavedStateMap?
) : NavModel<NavTarget, State>, Destroyable, BackPressHandler {

    constructor(
        backPressHandler: BackPressHandlerStrategy<NavTarget, State> = DontHandleBackPress(),
        operationStrategy: OperationStrategy<NavTarget, State> = ExecuteImmediately(),
        screenResolver: OnScreenStateResolver<State>,
        initialElements: NavElements<NavTarget, State>,
        scope: CoroutineScope = CoroutineScope(EmptyCoroutineContext + Dispatchers.Unconfined),
        finalState: State?,
        key: String = KEY_NAV_MODEL,
        savedStateMap: SavedStateMap?
    ) : this(
        backPressHandler = backPressHandler,
        operationStrategy = operationStrategy,
        screenResolver = screenResolver,
        initialElements = initialElements,
        scope = scope,
        finalStates = setOfNotNull(finalState),
        key = key,
        savedStateMap = savedStateMap
    )

    private val state: MutableStateFlow<NavElements<NavTarget, State>> by lazy {
        MutableStateFlow(savedStateMap?.restoreHistory() ?: initialElements)
    }

    override val elements: StateFlow<NavElements<NavTarget, State>> get() = state

    override val screenState: StateFlow<NavModelAdapter.ScreenState<NavTarget, State>> by lazy {
        state
            .map { elements ->
                NavModelAdapter.ScreenState(
                    onScreen = elements.filter { screenResolver.isOnScreen(it) },
                    offScreen = elements.filterNot { screenResolver.isOnScreen(it) },
                )
            }
            .stateIn(scope, SharingStarted.Eagerly, NavModelAdapter.ScreenState())
    }

    override val onBackPressedCallback: OnBackPressedCallback by lazy {
        val callback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                backPressHandler.onBackPressed()
            }
        }
        scope.launch {
            backPressHandler.canHandleBackPress.collect {
                callback.isEnabled = it
            }
        }
        callback
    }

    init {
        backPressHandler.init(this, scope)
        operationStrategy.init(this, scope, ::execute)
    }

    override fun accept(operation: Operation<NavTarget, State>) {
        operationStrategy.accept(operation)
    }

    protected fun updateState(block: (NavElements<NavTarget, State>) -> NavElements<NavTarget, State>) {
        state.update { currentState ->
            val newState = block(currentState)
            sanitizeOffScreenTransitions(newState)
        }
    }

    private fun execute(operation: Operation<NavTarget, State>) {
        updateState {
            if (operation.isApplicable(it)) {
                operation(it)
            } else {
                it
            }
        }
    }

    /**
     * Off screen transitions can't finish without [onTransitionFinished] callback from UI.
     * In case if we have any, lets finish them instantly.
     */
    protected fun sanitizeOffScreenTransitions(
        state: NavElements<NavTarget, State>
    ): NavElements<NavTarget, State> =
        state.mapNotNull {
            if (screenResolver.isOnScreen(it)) {
                it
            } else {
                it.finishTransitionOrRemove()
            }
        }

    override fun onTransitionFinished(keys: Collection<NavKey<NavTarget>>) {
        if (keys.isEmpty()) return
        state.update { list ->
            list.mapNotNull {
                if (it.key in keys) {
                    it.finishTransitionOrRemove()
                } else {
                    it
                }
            }
        }
    }

    protected open fun NavElement<NavTarget, State>.finishTransitionOrRemove(): NavElement<NavTarget, State>? =
        if (targetState.isFinalState) {
            null
        } else {
            onTransitionFinished()
        }

    override fun saveInstanceState(state: MutableSavedStateMap) {
        state[key] =
            elements.value.mapNotNull {
                // Sanitize outputs, removing all transitions
                if (it.targetState.isFinalState) {
                    null
                } else {
                    it.onTransitionFinished()
                }
            }
    }

    override fun destroy() {
        scope.cancel()
    }

    protected val State.isFinalState
        get() = finalStates.contains(this)

    private fun SavedStateMap.restoreHistory() =
        this[key] as? NavElements<NavTarget, State>

    companion object {
        const val KEY_NAV_MODEL = "NavModel"
    }

}
