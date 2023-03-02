package com.bumble.appyx.navigation.composable

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import com.bumble.appyx.interactions.core.InteractionModel
import com.bumble.appyx.interactions.core.ui.FrameModel
import com.bumble.appyx.interactions.core.ui.gesture.GestureSpec
import com.bumble.appyx.interactions.core.ui.TransitionBounds
import com.bumble.appyx.interactions.core.ui.UiContext
import com.bumble.appyx.navigation.node.ParentNode
import gestureModifier
import kotlinx.coroutines.flow.map
import kotlin.math.roundToInt
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
                    key = frameModel.element,
                    gestureSpec = gestureSpec
                )
            )
        }
    }
) {

    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()
    val screenWidthPx = (LocalConfiguration.current.screenWidthDp * density.density).roundToInt()
    val screenHeightPx = (LocalConfiguration.current.screenHeightDp * density.density).roundToInt()
    var uiContext by remember { mutableStateOf<UiContext?>(null) }

    LaunchedEffect(uiContext) {
        val uiContext = uiContext
        if (uiContext != null) {
            interactionModel.updateContext(uiContext)
        }
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .composed {
                val clipToBounds by interactionModel.clipToBounds.collectAsState(initial = false)
                if (clipToBounds) {
                    clipToBounds()
                } else {
                    this
                }
            }
            .onGloballyPositioned {
                uiContext = UiContext(
                    TransitionBounds(
                        density = density,
                        widthPx = it.size.width,
                        heightPx = it.size.height,
                        containerBoundsInRoot = it.boundsInRoot(),
                        screenWidthPx = screenWidthPx,
                        screenHeightPx = screenHeightPx
                    ),
                    coroutineScope
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

        val framesFlow = remember {
            interactionModel.frames
                .map { list ->
                    list
                        .filter { clazz.isInstance(it.element.interactionTarget) }
                }
        }

        val visibleFrames = framesFlow.collectAsState(initial = emptyList())
        val saveableStateHolder = rememberSaveableStateHolder()

        visibleFrames.value
            .forEach { frameModel ->
                key(frameModel.element.id) {
                    frameModel.animationContainer()
                    val isVisible by frameModel.visibleState.collectAsState(initial = false)
                    if (isVisible) {
                        Child(
                            frameModel,
                            saveableStateHolder,
                            block
                        )
                    }
                }
            }
    }
}
