@file:Suppress("MagicNumber")

package com.bumble.appyx.components.experimental.promoter.ui

import androidx.compose.animation.core.SpringSpec
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.components.experimental.promoter.PromoterModel
import com.bumble.appyx.components.experimental.promoter.PromoterModel.State.ElementState
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.helper.DefaultAnimationSpec
import com.bumble.appyx.interactions.core.ui.property.impl.AngularPosition
import com.bumble.appyx.interactions.core.ui.property.impl.RotationY
import com.bumble.appyx.interactions.core.ui.property.impl.RotationZ
import com.bumble.appyx.interactions.core.ui.property.impl.Scale
import com.bumble.appyx.interactions.core.ui.property.impl.position.BiasAlignment.InsideAlignment.Companion.Center
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionInside
import com.bumble.appyx.interactions.core.ui.state.MatchedTargetUiState
import com.bumble.appyx.transitionmodel.BaseVisualisation
import kotlin.math.min

class PromoterVisualisation<InteractionTarget : Any>(
    uiContext: UiContext,
    uiAnimationSpec: SpringSpec<Float> = DefaultAnimationSpec
) : BaseVisualisation<InteractionTarget, PromoterModel.State<InteractionTarget>, MutableUiState, TargetUiState>(
    uiContext = uiContext,
    defaultAnimationSpec = uiAnimationSpec,
) {
    init {
        createTargetUiStates(0f)
    }

    override fun PromoterModel.State<InteractionTarget>.toUiTargets():
            List<MatchedTargetUiState<InteractionTarget, TargetUiState>> =
        elements.map {
            MatchedTargetUiState(
                it.first, when (it.second) {
                    ElementState.CREATED -> created
                    ElementState.STAGE1 -> stage1
                    ElementState.STAGE2 -> stage2
                    ElementState.STAGE3 -> stage3
                    ElementState.STAGE4 -> stage4
                    ElementState.STAGE5 -> stage5
                    else -> destroyed
                }
            )
        }

    override fun mutableUiStateFor(
        uiContext: UiContext,
        targetUiState: TargetUiState
    ): MutableUiState =
        targetUiState.toMutableState(uiContext)

    override fun updateBounds(transitionBounds: TransitionBounds) {
        super.updateBounds(transitionBounds)
        val halfWidthDp = transitionBounds.widthDp.value / 2
        val halfHeightDp = transitionBounds.heightDp.value / 2
        val radius = min(halfWidthDp, halfHeightDp) * 0.8f
        createTargetUiStates(radius)
    }

    private lateinit var created: TargetUiState
    private lateinit var stage1: TargetUiState
    private lateinit var stage2: TargetUiState
    private lateinit var stage3: TargetUiState
    private lateinit var stage4: TargetUiState
    private lateinit var stage5: TargetUiState
    private lateinit var destroyed: TargetUiState

    @Suppress("LongMethod")
    private fun createTargetUiStates(radius: Float) {
        created = TargetUiState(
            position = PositionInside.Target(alignment = Center),
            angularPosition = AngularPosition.Target(
                AngularPosition.Value(
                    radius = radius,
                    angleDegrees = 0f
                )
            ),
            scale = Scale.Target(0f),
            rotationY = RotationY.Target(0f),
            rotationZ = RotationZ.Target(0f),
        )

        stage1 = TargetUiState(
            position = PositionInside.Target(alignment = Center),
            angularPosition = AngularPosition.Target(
                AngularPosition.Value(
                    radius = radius,
                    angleDegrees = 0f
                )
            ),
            scale = Scale.Target(0.25f),
            rotationY = RotationY.Target(0f),
            rotationZ = RotationZ.Target(0f)
        )

        stage2 = TargetUiState(
            position = PositionInside.Target(alignment = Center),
            angularPosition = AngularPosition.Target(
                AngularPosition.Value(
                    radius = radius,
                    angleDegrees = 90f
                )
            ),
            scale = Scale.Target(0.45f),
            rotationY = RotationY.Target(0f),
            rotationZ = RotationZ.Target(0f),
        )

        stage3 = TargetUiState(
            position = PositionInside.Target(alignment = Center),
            angularPosition = AngularPosition.Target(
                AngularPosition.Value(
                    radius = radius,
                    angleDegrees = 180f
                )
            ),
            scale = Scale.Target(0.65f),
            rotationY = RotationY.Target(0f),
            rotationZ = RotationZ.Target(0f),
        )

        stage4 = TargetUiState(
            position = PositionInside.Target(alignment = Center),
            angularPosition = AngularPosition.Target(
                AngularPosition.Value(
                    radius = radius,
                    angleDegrees = 270f
                )
            ),
            scale = Scale.Target(0.85f),
            rotationY = RotationY.Target(0f),
            rotationZ = RotationZ.Target(0f),
        )

        stage5 = TargetUiState(
            position = PositionInside.Target(alignment = Center),
            angularPosition = AngularPosition.Target(
                AngularPosition.Value(
                    radius = 0f,
                    angleDegrees = 270f
                )
            ),
            scale = Scale.Target(1f),
            rotationY = RotationY.Target(360f),
            rotationZ = RotationZ.Target(0f),
        )

        destroyed = TargetUiState(
            position = PositionInside.Target(
                alignment = Center,
                offset = DpOffset(500.dp, (-200).dp)
            ),
            angularPosition = AngularPosition.Target(
                AngularPosition.Value(
                    radius = radius,
                    angleDegrees = 0f
                )
            ),
            scale = Scale.Target(0f),
            rotationY = RotationY.Target(360f),
            rotationZ = RotationZ.Target(540f),
        )
    }
}

