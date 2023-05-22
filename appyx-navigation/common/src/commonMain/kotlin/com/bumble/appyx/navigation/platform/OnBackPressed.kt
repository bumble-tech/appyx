package com.bumble.appyx.navigation.platform

abstract class OnBackPressedCallback(var isEnabled: Boolean) {

    abstract fun handleOnBackPressed()

    open fun remove() {}
}

interface OnBackPressedDispatcher {
    fun addCallback(lifecycleOwner: PlatformLifecycleOwner, callback: OnBackPressedCallback)
}

interface OnBackPressedDispatcherProvider : () -> OnBackPressedDispatcher?
