package com.bumble.appyx.interactions.core.inputsource

import com.bumble.appyx.interactions.core.Operation

interface InputSource<NavTarget, ModelState> {

    fun operation(operation: Operation<ModelState>)
}
