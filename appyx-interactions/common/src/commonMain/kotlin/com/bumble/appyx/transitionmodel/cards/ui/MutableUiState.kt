package com.bumble.appyx.transitionmodel.cards.ui

import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.ui.Modifier
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.property.impl.Position
import com.bumble.appyx.interactions.core.ui.property.impl.RotationZ
import com.bumble.appyx.interactions.core.ui.property.impl.Scale
import com.bumble.appyx.interactions.core.ui.property.impl.ZIndex
import com.bumble.appyx.interactions.core.ui.state.BaseMutableUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class MutableUiState(
    uiContext: UiContext,
    val scale: Scale,
    val position: Position,
    val rotationZ: RotationZ,
    val zIndex: ZIndex
) : BaseMutableUiState<MutableUiState, TargetUiState>(
    uiContext = uiContext,
    motionProperties = listOf(scale, position, rotationZ, zIndex)
) {

    override val modifier: Modifier
        get() = Modifier.then(scale.modifier)
            .then(position.modifier)
            .then(rotationZ.modifier)
            .then(zIndex.modifier)

    override suspend fun snapTo(scope: CoroutineScope, target: TargetUiState) {
        scope.launch {
            scale.snapTo(target.scale.value)
            position.snapTo(target.position.value)
            rotationZ.snapTo(target.rotationZ.value)
            zIndex.snapTo(target.zIndex.value)
        }
    }

    override suspend fun animateTo(
        scope: CoroutineScope,
        target: TargetUiState,
        springSpec: SpringSpec<Float>,
    ) {
        listOf(
            scope.async {
                scale.animateTo(
                    target.scale.value,
                    spring(springSpec.dampingRatio, springSpec.stiffness)
                )
            },
            scope.async {
                position.animateTo(
                    target.position.value,
                    spring(springSpec.dampingRatio, springSpec.stiffness)
                )
            },
            scope.async {
                rotationZ.animateTo(
                    target.rotationZ.value,
                    spring(springSpec.dampingRatio, springSpec.stiffness)
                )
            },
            scope.async {
                zIndex.animateTo(
                    target.zIndex.value,
                    spring(springSpec.dampingRatio, springSpec.stiffness)
                )
            }).awaitAll()
    }

    override fun lerpTo(scope: CoroutineScope, start: TargetUiState, end: TargetUiState, fraction: Float) {
        scope.launch {
            scale.lerpTo(start.scale, end.scale, fraction)
            position.lerpTo(start.position, end.position, fraction)
            rotationZ.lerpTo(start.rotationZ, end.rotationZ, fraction)
            zIndex.lerpTo(start.zIndex, end.zIndex, fraction)
        }
    }
}
