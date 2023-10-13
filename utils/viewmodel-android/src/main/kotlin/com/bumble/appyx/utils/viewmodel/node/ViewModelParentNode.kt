package com.bumble.appyx.utils.viewmodel.node

import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.bumble.appyx.interactions.core.model.AppyxComponent
import com.bumble.appyx.navigation.lifecycle.DefaultPlatformLifecycleObserver
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize
import com.bumble.appyx.utils.viewmodel.integration.ActivityIntegrationPointWithViewModel

abstract class ViewModelParentNode(
    buildContext: BuildContext,
    node: AppyxComponent<InteractionTarget, *>,
) : ParentNode<ViewModelParentNode.InteractionTarget>(
    buildContext = buildContext,
    appyxComponent = node
), ViewModelStoreOwner {

    sealed class InteractionTarget : Parcelable {
        @Parcelize
        abstract class Child(val index: Int) : InteractionTarget()
    }

    private val nodeViewModelStore by lazy {
        (integrationPoint as ActivityIntegrationPointWithViewModel).viewModel.getViewModelStoreForNode(
            id
        )
    }

    init {
        lifecycle.addObserver(object : DefaultPlatformLifecycleObserver {
            override fun onDestroy() {
                if (!(integrationPoint as ActivityIntegrationPointWithViewModel).isChangingConfigurations) {
                    (integrationPoint as ActivityIntegrationPointWithViewModel).viewModel.clear(id)
                }
            }
        })
    }

    override val viewModelStore: ViewModelStore
        get() = nodeViewModelStore
}
