package com.bumble.appyx.navigation.node.viewModel

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.bumble.appyx.utils.viewmodel.integration.ActivityIntegrationPointWithViewModel

class ActivityIntegrationPointWithExampleViewModel(
    activity: ComponentActivity,
    savedInstanceState: Bundle?,
) : ActivityIntegrationPointWithViewModel(activity, savedInstanceState) {

    override val viewModel = ViewModelExample.getInstance(activity)
}
