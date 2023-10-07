package com.bumble.appyx.components.internal.testdrive.ui.rotation

import androidx.compose.animation.core.SpringSpec
import com.bumble.appyx.components.internal.testdrive.TestDriveModel
import com.bumble.appyx.components.internal.testdrive.TestDriveModel.State.ElementState.A
import com.bumble.appyx.components.internal.testdrive.TestDriveModel.State.ElementState.B
import com.bumble.appyx.components.internal.testdrive.TestDriveModel.State.ElementState.C
import com.bumble.appyx.components.internal.testdrive.TestDriveModel.State.ElementState.D
import com.bumble.appyx.components.internal.testdrive.ui.md_light_blue_500
import com.bumble.appyx.components.internal.testdrive.ui.md_light_green_500
import com.bumble.appyx.components.internal.testdrive.ui.md_red_500
import com.bumble.appyx.components.internal.testdrive.ui.md_yellow_500
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.helper.DefaultAnimationSpec
import com.bumble.appyx.interactions.core.ui.property.impl.BackgroundColor
import com.bumble.appyx.interactions.core.ui.property.impl.RotationZ
import com.bumble.appyx.interactions.core.ui.property.impl.position.BiasAlignment.InsideAlignment
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionAlignment
import com.bumble.appyx.interactions.core.ui.state.MatchedTargetUiState
import com.bumble.appyx.transitionmodel.BaseVisualisation
import com.bumble.appyx.utils.multiplatform.AppyxLogger

class TestDriveRotationVisualisation<InteractionTarget : Any>(
    uiContext: UiContext,
    uiAnimationSpec: SpringSpec<Float> = DefaultAnimationSpec
) : BaseVisualisation<InteractionTarget, TestDriveModel.State<InteractionTarget>, MutableUiState, TargetUiState>(
    uiContext = uiContext,
    defaultAnimationSpec = uiAnimationSpec,
) {
    override fun TestDriveModel.State<InteractionTarget>.toUiTargets():
            List<MatchedTargetUiState<InteractionTarget, TargetUiState>> =
        listOf(
            MatchedTargetUiState(element, elementState.toTargetUiState()).also {
                AppyxLogger.d("TestDrive", "Matched $elementState -> UiState: ${it.targetUiState}")
            }
        )

    companion object {

        fun TestDriveModel.State.ElementState.toTargetUiState(): TargetUiState =
            when (this) {
                A -> topLeftCorner
                B -> topRightCorner
                C -> bottomRightCorner
                D -> bottomLeftCorner
            }

        private val topLeftCorner = TargetUiState(
            position = PositionAlignment.Target(InsideAlignment.TopStart),
            rotationZ = RotationZ.Target(0f),
            backgroundColor = BackgroundColor.Target(md_red_500)
        )

        private val topRightCorner = TargetUiState(
            position = PositionAlignment.Target(InsideAlignment.TopEnd),
            rotationZ = RotationZ.Target(180f),
            backgroundColor = BackgroundColor.Target(md_light_green_500)
        )

        private val bottomRightCorner = TargetUiState(
            position = PositionAlignment.Target(InsideAlignment.CenterEnd),
            rotationZ = RotationZ.Target(270f),
            backgroundColor = BackgroundColor.Target(md_yellow_500)
        )

        private val bottomLeftCorner = TargetUiState(
            position = PositionAlignment.Target(InsideAlignment.CenterStart),
            rotationZ = RotationZ.Target(540f),
            backgroundColor = BackgroundColor.Target(md_light_blue_500)
        )
    }

    override fun mutableUiStateFor(
        uiContext: UiContext,
        targetUiState: TargetUiState
    ): MutableUiState =
        targetUiState.toMutableState(uiContext)
}

