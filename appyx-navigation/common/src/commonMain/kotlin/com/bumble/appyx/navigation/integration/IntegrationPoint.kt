package com.bumble.appyx.navigation.integration

import androidx.compose.runtime.Stable

@Stable
abstract class IntegrationPoint : UpNavigationHandler {

    abstract val isChangingConfigurations: Boolean

    abstract fun onRootFinished()
}
