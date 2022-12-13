package com.bumble.appyx.sandbox.client.viewmodel

import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.bumble.appyx.core.integrationpoint.ActivityIntegrationPoint
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node

class ViewModelNode(
    buildContext: BuildContext,
) : Node(
    buildContext = buildContext
) {
    private val activity = (integrationPoint as ActivityIntegrationPoint).activity
    private val viewModel: MyViewModel by activity.viewModels { MyViewModel.Factory }

    init {
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                if (!activity.isChangingConfigurations) {
                    activity.viewModelStore.removeViewModel(viewModel.javaClass)
                }
            }
        })
    }

    @Composable
    override fun View(modifier: Modifier) {
        val counter by viewModel.flow.collectAsState(initial = 0)
        Box(modifier = modifier.fillMaxSize()) {
            Text(
                text = counter.toString(),
                fontSize = 38.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
