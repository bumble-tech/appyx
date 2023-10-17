package com.bumble.appyx.utils.viewmodel.integration

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.bumble.appyx.navigation.integration.ActivityIntegrationPoint

open class ActivityIntegrationPointWithViewModel(
    activity: ComponentActivity,
    savedInstanceState: Bundle?,
) : ActivityIntegrationPoint(activity, savedInstanceState) {

    open val viewModel = IntegrationPointViewModel.getInstance(activity)
}
