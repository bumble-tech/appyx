package com.bumble.appyx.core.navigation.transition

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.bumble.appyx.core.navigation.Operation

@Immutable
data class TransitionDescriptor<NavTarget : Parcelable, out State : Parcelable>(
    val params: TransitionParams,
    val operation: Operation<NavTarget, out State>,
    val element: NavTarget,
    val fromState: State,
    val toState: State
)
