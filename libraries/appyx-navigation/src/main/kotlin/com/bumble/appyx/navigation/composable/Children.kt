package com.bumble.appyx.navigation.composable

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import com.bumble.appyx.interactions.core.InteractionModel
import com.bumble.appyx.interactions.core.ui.FrameModel
import com.bumble.appyx.interactions.core.ui.GestureSpec
import com.bumble.appyx.interactions.core.ui.TransitionBounds
import com.bumble.appyx.navigation.node.ParentNode
import gestureModifier
import kotlin.reflect.KClass

@Composable
inline fun <reified NavTarget : Any, NavState : Any> ParentNode<NavTarget>.Children(
    interactionModel: InteractionModel<NavTarget, NavState>,
    modifier: Modifier = Modifier,
    gestureSpec: GestureSpec = GestureSpec(),
    noinline block: @Composable ChildrenTransitionScope<NavTarget, NavState>.() -> Unit = {
        children<NavTarget> { child, frameModel ->
            child(
                modifier = Modifier.gestureModifier(
                    interactionModel = interactionModel,
                    key = frameModel.navElement,
                    gestureSpec = gestureSpec
                )
            )
        }
    }
) {

    val density = LocalDensity.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged {
                interactionModel.updateBounds(
                    TransitionBounds(density, it.width, it.height)
                )
            }
    ) {
        block(
            ChildrenTransitionScope(
                interactionModel = interactionModel
            )
        )
    }

}

class ChildrenTransitionScope<NavTarget : Any, NavState : Any>(
    private val interactionModel: InteractionModel<NavTarget, NavState>
) {

    @Composable
    inline fun <reified V : NavTarget> ParentNode<NavTarget>.children(
        noinline block: @Composable (child: ChildRenderer, frameModel: FrameModel<NavTarget>) -> Unit
    ) {
        children(V::class, block)
    }

    @SuppressLint("ComposableNaming")
    @Composable
    fun ParentNode<NavTarget>.children(
        clazz: KClass<out NavTarget>,
        block: @Composable (child: ChildRenderer, frameModel: FrameModel<NavTarget>) -> Unit,
    ) {
        _children(clazz) { child, frameModel ->
            block(child, frameModel)
        }
    }

    @SuppressLint("ComposableNaming")
    @Composable
    private fun ParentNode<NavTarget>._children(
        clazz: KClass<out NavTarget>,
        block: @Composable (child: ChildRenderer, frameModel: FrameModel<NavTarget>) -> Unit
    ) {

        val frames = interactionModel.frames.collectAsState(listOf())
        val saveableStateHolder = rememberSaveableStateHolder()

        // TODO apply on/off screen logic
        frames.value
            .filter { clazz.isInstance(it.navElement.navTarget) }
            .forEach { frameModel ->
                val navKey = frameModel.navElement
                key(navKey.id) {
                    Child(
                        frameModel,
                        saveableStateHolder,
                        block
                    )
                }
            }
    }
}
