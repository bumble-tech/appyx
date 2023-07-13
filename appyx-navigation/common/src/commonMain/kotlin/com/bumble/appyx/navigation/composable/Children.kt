package com.bumble.appyx.navigation.composable

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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import com.bumble.appyx.interactions.core.model.BaseInteractionModel
import com.bumble.appyx.interactions.core.model.removedElements
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.output.ElementUiModel
import com.bumble.appyx.navigation.integration.LocalScreenSize
import com.bumble.appyx.navigation.node.ParentNode
import gestureModifier
import kotlin.math.roundToInt

@Composable
fun <InteractionTarget : Any, ModelState : Any> Children(
    parent: ParentNode<InteractionTarget>,
    interactionModel: BaseInteractionModel<InteractionTarget, ModelState>,
    modifier: Modifier = Modifier,
    clipToBounds: Boolean = false,
    block: @Composable ChildrenTransitionScope<InteractionTarget, ModelState>.() -> Unit = {
        parent.children { child: ChildRenderer, elementUiModel: ElementUiModel<InteractionTarget> ->
            child(
                modifier = Modifier.gestureModifier(
                    interactionModel = interactionModel,
                    key = elementUiModel.element,
                )
            )
        }
    }
) {
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()
    val screenWidthPx = (LocalScreenSize.current.widthDp * density.density).value.roundToInt()
    val screenHeightPx = (LocalScreenSize.current.heightDp * density.density).value.roundToInt()
    var uiContext by remember { mutableStateOf<UiContext?>(null) }

    LaunchedEffect(uiContext) {
        uiContext?.let { interactionModel.updateContext(it) }
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .then(if (clipToBounds) Modifier.clipToBounds() else Modifier)
            .onPlaced {
                uiContext = UiContext(
                    coroutineScope = coroutineScope,
                    transitionBounds = TransitionBounds(
                        density = density,
                        widthPx = it.size.width,
                        heightPx = it.size.height,
                        containerBoundsInRoot = it.boundsInRoot(),
                        screenWidthPx = screenWidthPx,
                        screenHeightPx = screenHeightPx
                    ),
                    clipToBounds = clipToBounds
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

@Composable
inline fun <reified InteractionTarget : Any, ModelState : Any> ParentNode<InteractionTarget>.Children(
    interactionModel: BaseInteractionModel<InteractionTarget, ModelState>,
    modifier: Modifier = Modifier,
    clipToBounds: Boolean = false,
    noinline block: @Composable ChildrenTransitionScope<InteractionTarget, ModelState>.() -> Unit = {
        children { child: ChildRenderer, elementUiModel: ElementUiModel<InteractionTarget> ->
            child(
                modifier = Modifier.gestureModifier(
                    interactionModel = interactionModel,
                    key = elementUiModel.element,
                )
            )
        }
    }
) {
    Children(this, interactionModel, modifier, clipToBounds, block)
}

class ChildrenTransitionScope<InteractionTarget : Any, NavState : Any>(
    private val interactionModel: BaseInteractionModel<InteractionTarget, NavState>
) {

    @Composable
    fun ParentNode<InteractionTarget>.children(
        block: @Composable (child: ChildRenderer, elementUiModel: ElementUiModel<InteractionTarget>) -> Unit
    ) {

        val framesFlow = remember {
            this@ChildrenTransitionScope.interactionModel.uiModels
        }

        val visibleFrames = framesFlow.collectAsState(initial = emptyList())
        val saveableStateHolder = rememberSaveableStateHolder()

        LaunchedEffect(this@ChildrenTransitionScope.interactionModel) {
            this@ChildrenTransitionScope.interactionModel
                .removedElements()
                .collect { deletedKeys ->
                    deletedKeys.forEach { navKey ->
                        saveableStateHolder.removeState(navKey)
                    }
                }
        }

        visibleFrames.value
            .forEach { uiModel ->
                key(uiModel.element.id) {
                    uiModel.persistentContainer()
                    val isVisible by uiModel.visibleState.collectAsState()
                    if (isVisible) {
                        Child(
                            uiModel,
                            saveableStateHolder,
                            block
                        )
                    }
                }
            }
    }
}