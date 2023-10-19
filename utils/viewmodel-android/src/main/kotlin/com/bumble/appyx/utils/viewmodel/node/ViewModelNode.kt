package com.bumble.appyx.utils.viewmodel.node

import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.bumble.appyx.navigation.lifecycle.DefaultPlatformLifecycleObserver
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.utils.viewmodel.integration.ActivityIntegrationPointWithViewModelStoreProvider

open class ViewModelNode(
    buildContext: BuildContext,
) : Node(buildContext), ViewModelStoreOwner {

    private val nodeViewModelStore by lazy {
        (integrationPoint as ActivityIntegrationPointWithViewModelStoreProvider).viewModelStoreProvider.getViewModelStoreForNode(
            id
        )
    }

    init {
        lifecycle.addObserver(object : DefaultPlatformLifecycleObserver {
            override fun onDestroy() {
                if (!(integrationPoint as ActivityIntegrationPointWithViewModelStoreProvider).isChangingConfigurations) {
                    (integrationPoint as ActivityIntegrationPointWithViewModelStoreProvider).viewModelStoreProvider.clear(id)
                }
            }
        })
    }

    override val viewModelStore: ViewModelStore
        get() = nodeViewModelStore
}
