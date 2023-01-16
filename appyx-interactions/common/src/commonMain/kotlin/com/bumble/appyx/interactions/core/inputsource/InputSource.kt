package com.bumble.appyx.interactions.core.inputsource

import com.bumble.appyx.interactions.core.Operation

interface InputSource<NavTarget, State> {
    fun operation(operation: Operation<NavTarget, State>)
}
