package com.bumble.appyx.navigation

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * TODO: Move to testing module for appyx 2.0
 */
class InternalAppyxTestActivity : AppyxTestActivity() {

    private val callback = object : OnBackPressedCallback(handleBackPress.value) {
        override fun handleOnBackPressed() {
            onBackPressedHandled = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch { handleBackPress.collect { callback.isEnabled = it } }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    companion object {
        val handleBackPress: MutableStateFlow<Boolean> = MutableStateFlow(false)
        @Volatile
        var onBackPressedHandled: Boolean = false

        fun reset() {
            handleBackPress.value = false
            onBackPressedHandled = false
        }
    }

}
