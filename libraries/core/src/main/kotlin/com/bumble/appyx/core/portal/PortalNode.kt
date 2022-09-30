package com.bumble.appyx.core.portal

import android.os.Parcelable
import androidx.activity.OnBackPressedCallback
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.coroutineScope
import com.bumble.appyx.Appyx
import com.bumble.appyx.core.children.ChildEntry
import com.bumble.appyx.core.children.nodeOrNull
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.navigation.BaseNavModel
import com.bumble.appyx.core.navigation.NavElement
import com.bumble.appyx.core.navigation.NavElements
import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.core.navigation.Operation
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.BackStackOnScreenResolver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

class PortalNode(
    buildContext: BuildContext,
    private val portalMultiStack: PortalMultiStack = PortalMultiStack(),
    private val rootNodeFactory: (BuildContext, PortalClientFactory) -> Node,
) : ParentNode<PortalNode.NavTarget>(
    buildContext = buildContext,
    navModel = portalMultiStack,
) {
    private val portalClientChildren =
        HashMap<PortalClient<out Any>, StateFlow<Map<out NavKey<out Any>, ChildEntry<out Any>>>>()
    private val portalClientFactory = PortalClientFactoryImpl { client ->
        val scope = CoroutineScope(lifecycle.coroutineScope.coroutineContext)
        client.onAttachListener = { children ->
            portalClientChildren[client] = children
            scope.launch {
                combine(client.navModel.screenState, children, ::Pair)
                    .distinctUntilChanged()
                    .collect { (state, children) ->
                        state.onScreen.forEach { onScreenElement ->
                            children[onScreenElement.key]?.nodeOrNull?.also {
                                it.lockCaller(this@PortalNode)
                                it.updateLifecycleState(lifecycle.currentState, this@PortalNode)
                            }
                        }
                        state.offScreen.forEach { offScreenElement ->
                            children[offScreenElement.key]?.nodeOrNull?.lockCaller(this@PortalNode)
                        }
                    }
            }
            portalMultiStack.add(client.navModel)
        }
        client.onDetachListener = {
            scope.cancel()
            portalClientChildren.remove(client)
        }
    }

    init {
        require(Appyx.defaultChildKeepMode == ChildEntry.KeepMode.KEEP) { "Portal supports only KEEP mode" }
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
        when (navTarget) {
            is NavTarget.Root -> rootNodeFactory(buildContext, portalClientFactory)
            is NavTarget.Client -> {
                portalClientChildren.values.forEach { stateFlow ->
                    stateFlow.value[navTarget.originalKey]?.also {
                        return@resolve requireNotNull(it.nodeOrNull)
                    }
                }
                error("Not found")
            }
        }

    @Composable
    override fun View(modifier: Modifier) {
        Children(navModel = portalMultiStack, modifier = modifier)
    }

    // TODO: Handle restoration order of children, it may produce different order of elements in backStacks
    class PortalMultiStack : BaseNavModel<NavTarget, BackStack.State>(
        savedStateMap = null, // Child routers will save the state
        finalState = BackStack.State.DESTROYED,
        screenResolver = BackStackOnScreenResolver,
    ) {
        private val backStacks = MutableStateFlow<List<PortalClientNavModel<*>>>(emptyList())

        override val initialElements: NavElements<NavTarget, BackStack.State> =
            listOf(
                NavElement(
                    key = NavKey(NavTarget.Root),
                    fromState = BackStack.State.ACTIVE,
                    targetState = BackStack.State.ACTIVE,
                    operation = Operation.Noop(),
                )
            )

        init {
            // Update inOnScreen property separately to make it easier to handle it later
            // TODO: Maybe merge in imperative manner for performance?
            scope.launch {
                backStacks
                    .flatMapLatest { list ->
                        combine(list.map { it.elements }) { array -> list to array }
                    }
                    .collect { (stacks, elements) ->
                        var visibleHandled = false
                        for (i in stacks.indices.reversed()) {
                            if (elements[i].isNotEmpty() && !visibleHandled) {
                                stacks[i].setVisibility(true)
                                visibleHandled = false
                            } else {
                                stacks[i].setVisibility(false)
                            }
                        }
                    }
            }
            // Collect and build current state
            scope.launch {
                backStacks
                    .flatMapLatest { list ->
                        combine(list.map { it.elements }) { array -> list to array }
                    }
                    .collect { (stacks, elements) ->
                        val finalElements =
                            ArrayList<NavElement<NavTarget, BackStack.State>>()
                        elements.forEach { list ->
                            list.forEach { element ->
                                finalElements.add(
                                    NavElement(
                                        key = NavKey(NavTarget.Client(element.key), element.key.id),
                                        fromState = element.fromState,
                                        targetState = element.targetState,
                                        operation = Operation.Noop(),
                                    )
                                )
                            }
                        }
                        updateState {
                            if (finalElements.isEmpty()) {
                                listOf(
                                    it[0].transitionTo(BackStack.State.ACTIVE, Operation.Noop())
                                        .onTransitionFinished()
                                )
                            } else {
                                listOf(
                                    it[0].transitionTo(BackStack.State.STASHED, Operation.Noop())
                                ) + finalElements
                            }
                        }
                    }
            }
        }

        fun add(backStack: PortalClientNavModel<*>) {
            backStacks.update { it + backStack }
        }

        fun remove(backStack: PortalClientNavModel<*>) {
            backStacks.update { it - backStack }
        }

        override val onBackPressedCallback: OnBackPressedCallback by lazy {
            val callback = object : OnBackPressedCallback(false) {
                override fun handleOnBackPressed() {
                    backStacks.value.forEach {
                        it.onBackPressedCallbackList.forEach {
                            if (it.isEnabled) {
                                it.handleOnBackPressed()
                            }
                        }
                    }
                }
            }
            // TODO: Make the list observable!
            scope.launch {
                while (isActive) {
                    delay(100)
                    callback.isEnabled =
                        backStacks.value.any { it.onBackPressedCallbackList.any { it.isEnabled } }
                }
            }
            callback
        }

    }

    sealed class NavTarget : Parcelable {
        @Parcelize
        object Root : NavTarget()

        @Parcelize
        data class Client(val originalKey: NavKey<*>) : NavTarget()
    }

}