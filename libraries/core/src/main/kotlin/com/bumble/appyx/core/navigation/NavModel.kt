package com.bumble.appyx.core.navigation

import android.os.Parcelable
import androidx.compose.runtime.Stable
import com.bumble.appyx.core.plugin.BackPressHandler
import com.bumble.appyx.core.plugin.SavesInstanceState
import com.bumble.appyx.core.plugin.UpNavigationHandler
import kotlinx.coroutines.flow.StateFlow

@Stable
interface NavModel<NavTarget : Parcelable, State : Parcelable> : NavModelAdapter<NavTarget, State>,
    UpNavigationHandler,
    SavesInstanceState,
    BackPressHandler {

    val elements: StateFlow<NavElements<NavTarget, out State>>

    fun onTransitionFinished(key: NavKey<NavTarget>) {
        onTransitionFinished(listOf(key))
    }

    fun onTransitionFinished(keys: Collection<NavKey<NavTarget>>)

    fun accept(operation: Operation<NavTarget, State>) = Unit

    override fun handleUpNavigation(): Boolean =
        handleOnBackPressed()

}
