package com.bumble.appyx.core.navigation2.inputsource

import com.bumble.appyx.core.navigation2.Operation
import com.bumble.appyx.core.navigation2.BaseNavModel

class InstantInputSource<Target : Any, State>(
    private val navModel: BaseNavModel<Target, State>,
) {
    // TODO extension methods over InputSource, e.g.:
    //  inputSource.push(A)
    fun operation(operation: Operation<Target, State>) {
        navModel.enqueue(operation)
        navModel.setProgress(progress = 1f)
    }
}
