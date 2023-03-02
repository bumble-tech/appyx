package com.bumble.appyx.transitionmodel.testdrive.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.core.model.transition.BaseOperation
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.transitionmodel.testdrive.TestDrive
import com.bumble.appyx.transitionmodel.testdrive.TestDriveModel

@Parcelize
data class Next<InteractionTarget>(
    override val mode: Operation.Mode = Operation.Mode.KEYFRAME
) : BaseOperation<TestDriveModel.State<InteractionTarget>>() {

    override fun isApplicable(state: TestDriveModel.State<InteractionTarget>): Boolean =
        true

    override fun createFromState(baseLineState: TestDriveModel.State<InteractionTarget>): TestDriveModel.State<InteractionTarget> =
        baseLineState

    override fun createTargetState(fromState: TestDriveModel.State<InteractionTarget>): TestDriveModel.State<InteractionTarget> =
        fromState.copy(
            elementState = fromState.elementState.next()
        )
}

fun <InteractionTarget : Any> TestDrive<InteractionTarget>.next(
    mode: Operation.Mode = Operation.Mode.KEYFRAME,
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec
) {
    operation(Next(mode), animationSpec)
}
