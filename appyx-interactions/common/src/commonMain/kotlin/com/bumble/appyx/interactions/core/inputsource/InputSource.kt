package com.bumble.appyx.interactions.core.inputsource

import com.bumble.appyx.interactions.core.Operation
import com.bumble.appyx.interactions.core.Operation.Mode
import com.bumble.appyx.interactions.core.Operation.Mode.KEYFRAME

interface InputSource<NavTarget, ModelState> {

    fun operation(operation: Operation<ModelState>, mode: Mode = KEYFRAME)
}
