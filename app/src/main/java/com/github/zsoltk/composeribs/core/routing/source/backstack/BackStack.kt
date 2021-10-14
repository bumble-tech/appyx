package com.github.zsoltk.composeribs.core.routing.source.backstack

import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.TransitionState.CREATED
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.TransitionState.DESTROYED
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.TransitionState.ON_SCREEN
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.TransitionState.STASHED_IN_BACK_STACK
import com.github.zsoltk.composeribs.core.unsuspendedMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.concurrent.atomic.AtomicInteger

open class BackStack<T>(
    initialElement: T,
) : RoutingSource<T, BackStack.TransitionState> {

    data class LocalRoutingKey<T>(
        override val routing: T,
        val uuid: Int,
    ) : RoutingKey<T>

    enum class TransitionState {
        CREATED, ON_SCREEN, STASHED_IN_BACK_STACK, DESTROYED,
    }

    private val tmpCounter = AtomicInteger(1)

    private val state = MutableStateFlow(
        listOf(
            BackStackElement(
                key = LocalRoutingKey(initialElement, tmpCounter.incrementAndGet()),
                fromState = ON_SCREEN,
                targetState = ON_SCREEN,
                onScreen = true,
            )
        )
    )

    override val all: StateFlow<List<BackStackElement<T>>> =
        state

    override val offScreen: StateFlow<List<BackStackElement<T>>> =
        state.unsuspendedMap { list -> list.filter { !it.onScreen } }

    override val onScreen: StateFlow<List<BackStackElement<T>>> =
        state.unsuspendedMap { list -> list.filter { !it.onScreen } }

    override val canHandleBackPress: StateFlow<Boolean> =
        state.unsuspendedMap { list -> list.count { it.targetState == STASHED_IN_BACK_STACK } > 0 }

    fun push(element: T) {
        state.update { list ->
            list.map {
                if (it.targetState == ON_SCREEN) {
                    it.copy(targetState = STASHED_IN_BACK_STACK)
                } else {
                    it
                }
            } + BackStackElement(
                key = LocalRoutingKey(element, tmpCounter.incrementAndGet()),
                fromState = CREATED,
                targetState = ON_SCREEN,
                onScreen = true
            )
        }
    }

    fun pop() {
        state.update { list ->
            val destroyIndex = list.indexOfLast { it.targetState == ON_SCREEN }
            val unStashIndex = list.indexOfLast { it.targetState == STASHED_IN_BACK_STACK }
            require(destroyIndex != -1) { "Nothing to destroy, state=$list" }
            require(unStashIndex != -1) { "Nothing to remove from stash, state=$list" }
            list.mapIndexed { index, element ->
                when (index) {
                    destroyIndex -> element.copy(targetState = DESTROYED)
                    unStashIndex -> element.copy(targetState = ON_SCREEN, onScreen = true)
                    else -> element
                }
            }
        }
    }

    override fun onTransitionFinished(key: RoutingKey<T>) {
        state.update { list ->
            list.mapNotNull {
                if (it.key == key) {
                    when (it.targetState) {
                        DESTROYED ->
                            null
                        STASHED_IN_BACK_STACK ->
                            it.copy(fromState = it.targetState, onScreen = false)
                        CREATED,
                        ON_SCREEN ->
                            it.copy(fromState = it.targetState, onScreen = true)
                    }
                } else {
                    it
                }
            }
        }
    }

    override fun onBackPressed() {
        pop()
    }
}
