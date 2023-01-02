package com.bumble.appyx.core.navigation.transition

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import com.bumble.appyx.core.composable.ChildTransitionScope

@Stable
interface TransitionHandler<T : Parcelable, S : Parcelable> {

    @Composable
    fun handle(
        descriptor: TransitionDescriptor<T, S>,
        onTransitionFinished: (S) -> Unit
    ): ChildTransitionScope<S>
}
