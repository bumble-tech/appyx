package com.bumble.appyx.core.navigation2.inputsource

import com.bumble.appyx.core.navigation2.Operation

interface InputSource<NavTarget, State> {
    fun operation(operation: Operation<NavTarget, State>)
}
