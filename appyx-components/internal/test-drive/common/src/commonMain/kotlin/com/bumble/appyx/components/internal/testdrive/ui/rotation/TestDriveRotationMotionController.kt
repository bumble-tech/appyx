package com.bumble.appyx.components.internal.testdrive.ui.rotation

import com.bumble.appyx.interactions.core.ui.helper.DefaultAnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.components.internal.testdrive.TestDriveModel
import com.bumble.appyx.components.internal.testdrive.TestDriveModel.State.ElementState.A
import com.bumble.appyx.components.internal.testdrive.TestDriveModel.State.ElementState.B
import com.bumble.appyx.components.internal.testdrive.TestDriveModel.State.ElementState.C
import com.bumble.appyx.components.internal.testdrive.TestDriveModel.State.ElementState.D
import com.bumble.appyx.components.internal.testdrive.ui.md_light_blue_500
import com.bumble.appyx.components.internal.testdrive.ui.md_light_green_500
import com.bumble.appyx.components.internal.testdrive.ui.md_red_500
import com.bumble.appyx.components.internal.testdrive.ui.md_yellow_500
import com.bumble.appyx.interactions.AppyxLogger
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.property.impl.BackgroundColor
import com.bumble.appyx.interactions.core.ui.property.impl.Position
import com.bumble.appyx.interactions.core.ui.property.impl.RotationZ
import com.bumble.appyx.interactions.core.ui.state.MatchedTargetUiState
import com.bumble.appyx.transitionmodel.BaseMotionController

class TestDriveRotationMotionController<InteractionTarget : Any>(
    uiContext: UiContext,
    uiAnimationSpec: SpringSpec<Float> = DefaultAnimationSpec
) : BaseMotionController<InteractionTarget, TestDriveModel.State<InteractionTarget>, MutableUiState, TargetUiState>(
    uiContext = uiContext,
    defaultAnimationSpec = uiAnimationSpec,
) {
    override fun TestDriveModel.State<InteractionTarget>.toUiTargets(): List<MatchedTargetUiState<InteractionTarget, TargetUiState>> =
        listOf(
            MatchedTargetUiState(element, elementState.toTargetUiState()).also {
                AppyxLogger.d("TestDrive", "Matched $elementState -> UiState: ${it.targetUiState}")
            }
        )

    companion object {
        val offsetA = DpOffset(0.dp, 0.dp)
        val offsetB = DpOffset(200.dp, 0.dp)
        val offsetC = DpOffset(200.dp, 300.dp)
        val offsetD = DpOffset(0.dp, 300.dp)

        fun TestDriveModel.State.ElementState.toTargetUiState(): TargetUiState =
            when (this) {
                A -> a
                B -> b
                C -> c
                D -> d
            }

        private val a = TargetUiState(
            position = Position.Target(offsetA),
            rotationZ = RotationZ.Target(0f),
            backgroundColor = BackgroundColor.Target(md_red_500)
        )

        private val b = TargetUiState(
            position = Position.Target(offsetB),
            rotationZ = RotationZ.Target(90f),
            backgroundColor = BackgroundColor.Target(md_light_green_500)
        )

        private val c = TargetUiState(
            position = Position.Target(offsetC),
            rotationZ = RotationZ.Target(180f),
            backgroundColor = BackgroundColor.Target(md_yellow_500)
        )

        private val d = TargetUiState(
            position = Position.Target(offsetD),
            rotationZ = RotationZ.Target(270f),
            backgroundColor = BackgroundColor.Target(md_light_blue_500)
        )
    }

    override fun mutableUiStateFor(
        uiContext: UiContext,
        targetUiState: TargetUiState
    ): MutableUiState =
        targetUiState.toMutableState(uiContext)
}

