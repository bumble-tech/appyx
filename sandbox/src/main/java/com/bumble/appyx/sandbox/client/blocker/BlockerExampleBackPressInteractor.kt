package com.bumble.appyx.sandbox.client.blocker

import androidx.activity.OnBackPressedCallback
import androidx.compose.runtime.Stable
import com.bumble.appyx.core.plugin.BackPressHandler
import com.bumble.appyx.core.plugin.Destroyable
import com.bumble.appyx.core.plugin.UpNavigationHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@Stable
class BlockerExampleBackPressInteractor : BackPressHandler, UpNavigationHandler, Destroyable {
    private val scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    val interceptBackClicks: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val allowNavigateUp: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val errors: MutableSharedFlow<Long> = MutableSharedFlow(extraBufferCapacity = 1)

    override val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(interceptBackClicks.value) {
            override fun handleOnBackPressed() {
                errors.tryEmit(System.currentTimeMillis())
            }
        }

    init {
        scope.launch {
            interceptBackClicks.collect { onBackPressedCallback.isEnabled = it }
        }
    }

    override fun destroy() {
        scope.cancel()
    }

    override fun handleUpNavigation(): Boolean =
        !allowNavigateUp.value

}
