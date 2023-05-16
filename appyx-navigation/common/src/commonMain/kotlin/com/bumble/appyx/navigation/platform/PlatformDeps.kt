package com.bumble.appyx.navigation.platform

interface PlatformDeps {
    val lifecycleRegistryProvider: LifecycleRegistryProvider
    val onBackPressedDispatcherProvider: OnBackPressedDispatcherProvider
    val localLifecycleOwnerProvider: LocalLifecycleOwnerProvider
}
