package com.bumble.appyx.benchmark.app.mosaic

import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.ui.Modifier
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.property.impl.AngularPosition
import com.bumble.appyx.interactions.core.ui.property.impl.RotationY
import com.bumble.appyx.interactions.core.ui.property.impl.RotationZ
import com.bumble.appyx.interactions.core.ui.property.impl.RoundedCorners
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionAlignment
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionOffset
import com.bumble.appyx.interactions.core.ui.state.BaseMutableUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

@Suppress("LongParameterList")
class MutableUiState(
    uiContext: UiContext,
    val position: PositionAlignment,
    val positionOffset: PositionOffset,
    val angularPosition: AngularPosition,
    val rotationZ: RotationZ,
    val rotationY: RotationY,
    val roundedCorners: RoundedCorners,
) : BaseMutableUiState<TargetUiState>(
    uiContext = uiContext,
    motionProperties = listOf(
        position, positionOffset, angularPosition, rotationZ, rotationY,
        roundedCorners
    ),
) {
    public override val combinedMotionPropertyModifier: Modifier = Modifier
        .then(position.modifier)
        .then(positionOffset.modifier)
        .then(angularPosition.modifier)
        .then(rotationZ.modifier)
        .then(rotationY.modifier)
        .then(roundedCorners.modifier)


    override suspend fun animateTo(
        scope: CoroutineScope,
        target: TargetUiState,
        springSpec: SpringSpec<Float>,
    ) {
        listOf(
            scope.async {
                position.animateTo(
                    target.position.value,
                    spring(springSpec.dampingRatio, springSpec.stiffness),
                )
            },
            scope.async {
                positionOffset.animateTo(
                    target.positionOffset.value,
                    spring(springSpec.dampingRatio, springSpec.stiffness),
                )
            },
            scope.async {
                angularPosition.animateTo(
                    target.angularPosition.value,
                    spring(springSpec.dampingRatio, springSpec.stiffness),
                )
            },
            scope.async {
                rotationZ.animateTo(
                    target.rotationZ.value,
                    spring(springSpec.dampingRatio, springSpec.stiffness),
                )
            },
            scope.async {
                rotationY.animateTo(
                    target.rotationY.value,
                    spring(springSpec.dampingRatio, springSpec.stiffness),
                )
            },
            scope.async {
                roundedCorners.animateTo(
                    target.roundedCorners.value,
                    spring(springSpec.dampingRatio, springSpec.stiffness),
                )
            },
        ).awaitAll()
    }

    override suspend fun snapTo(target: TargetUiState): Unit {
        position.snapTo(target.position.value)
        positionOffset.snapTo(target.positionOffset.value)
        angularPosition.snapTo(target.angularPosition.value)
        rotationZ.snapTo(target.rotationZ.value)
        rotationY.snapTo(target.rotationY.value)
        roundedCorners.snapTo(target.roundedCorners.value)
    }

    override fun lerpTo(
        scope: CoroutineScope,
        start: TargetUiState,
        end: TargetUiState,
        fraction: Float,
    ) {
        scope.launch {
            position.lerpTo(start.position, end.position, fraction)
            positionOffset.lerpTo(start.positionOffset, end.positionOffset, fraction)
            angularPosition.lerpTo(start.angularPosition, end.angularPosition, fraction)
            rotationZ.lerpTo(start.rotationZ, end.rotationZ, fraction)
            rotationY.lerpTo(start.rotationY, end.rotationY, fraction)
            roundedCorners.lerpTo(start.roundedCorners, end.roundedCorners, fraction)
        }
    }
}

fun TargetUiState.toMutableUiState(uiContext: UiContext): MutableUiState = MutableUiState(
    uiContext = uiContext,
    position = PositionAlignment(uiContext.coroutineScope, position),
    positionOffset = PositionOffset(uiContext.coroutineScope, positionOffset),
    angularPosition = AngularPosition(uiContext.coroutineScope, angularPosition),
    rotationZ = RotationZ(uiContext.coroutineScope, rotationZ),
    rotationY = RotationY(uiContext.coroutineScope, rotationY),
    roundedCorners = RoundedCorners(uiContext.coroutineScope, roundedCorners),
)
