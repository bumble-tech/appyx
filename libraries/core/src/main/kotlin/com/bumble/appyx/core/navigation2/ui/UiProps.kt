package com.bumble.appyx.core.navigation2.ui

import com.bumble.appyx.core.navigation2.BaseNavModel

interface UiProps<Target, State> {

    fun map(segment: BaseNavModel.State<Target, State>): List<Modifiers<Target, State>>

    companion object {
        fun lerp(a: Float, b: Float, progress: Float): Float =
            a + progress * (b - a)
    }
}
