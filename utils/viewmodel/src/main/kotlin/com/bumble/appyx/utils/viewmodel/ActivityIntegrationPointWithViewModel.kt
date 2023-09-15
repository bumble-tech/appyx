package com.bumble.appyx.utils.viewmodel

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.bumble.appyx.navigation.integration.ActivityIntegrationPoint

class ActivityIntegrationPointWithViewModel(
    private val activity: ComponentActivity,
    savedInstanceState: Bundle?,
) : ActivityIntegrationPoint(activity, savedInstanceState) {

    val viewModel = IntegrationPointViewModel.getInstance(activity)

    fun isChangingConfigurations(): Boolean = activity.isChangingConfigurations
}