package com.bumble.appyx.navigation

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import com.bumble.appyx.testing.ui.rules.AppyxTestActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

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
