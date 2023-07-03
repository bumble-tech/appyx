package com.bumble.appyx.interactions.core.modifiers

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.PointerInputModifierNode
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.IntSize

@OptIn(ExperimentalComposeUiApi::class)
class OnPointerEventNode(var callback: (PointerEvent) -> Unit) :
    PointerInputModifierNode, Modifier.Node() {
    override fun onPointerEvent(
        pointerEvent: PointerEvent,
        pass: PointerEventPass,
        bounds: IntSize
    ) {
        if (pass == PointerEventPass.Initial) {
            callback(pointerEvent)
        }
    }

    override fun sharePointerInputWithSiblings(): Boolean = false

    override fun onCancelPointerInput() {
        // Do nothing
    }
}

@OptIn(ExperimentalComposeUiApi::class)
data class PointerInputElement(
    val callback: (PointerEvent) -> Unit
) : ModifierNodeElement<OnPointerEventNode>() {
    override fun create() = OnPointerEventNode(callback)
    override fun update(node: OnPointerEventNode): OnPointerEventNode {
        node.callback = callback
        return node
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "onPointerEvent"
        properties["callback"] = callback
    }
}

fun Modifier.onPointerEvent(callback: (PointerEvent) -> Unit) =
    this then PointerInputElement(callback)
