package com.bumble.appyx.navigation.integration

import androidx.compose.runtime.Stable
import com.bumble.appyx.navigation.navigation.upnavigation.UpNavigationHandler

@Stable
abstract class IntegrationPoint : UpNavigationHandler {

    abstract val isChangingConfigurations: Boolean

    abstract fun onRootFinished()
}
