package com.bumble.appyx.utils.interop.ribs

import android.content.Context
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.Lifecycle
import com.badoo.ribs.compose.ComposeRibView
import com.badoo.ribs.compose.ComposeView
import com.badoo.ribs.core.view.RibView
import com.badoo.ribs.core.view.ViewFactory
import com.badoo.ribs.core.view.ViewFactoryBuilder
import com.bumble.appyx.navigation.node.AbstractNode
import com.bumble.appyx.utils.interop.ribs.InteropView.Dependency

interface InteropView : RibView {

    interface Dependency<N : AbstractNode> {
        val appyxNode: N
        val onBackPressedDispatcherOwner: OnBackPressedDispatcherOwner
    }
}

internal class InteropViewImpl private constructor(
    override val context: Context,
    lifecycle: Lifecycle,
    private val appyxNode: AbstractNode,
    private val onBackPressedDispatcherOwner: OnBackPressedDispatcherOwner,
) : InteropView, ComposeRibView(context, lifecycle) {

    override val composable: ComposeView
        get() = @Composable {
            CompositionLocalProvider(
                LocalOnBackPressedDispatcherOwner provides onBackPressedDispatcherOwner,
            ) {
                appyxNode.Compose()
            }
        }

    class Factory<N : AbstractNode> : ViewFactoryBuilder<Dependency<N>, InteropView> {
        override fun invoke(deps: Dependency<N>): ViewFactory<InteropView> =
            ViewFactory {
                InteropViewImpl(
                    context = it.parent.context,
                    lifecycle = it.lifecycle,
                    appyxNode = deps.appyxNode,
                    onBackPressedDispatcherOwner = deps.onBackPressedDispatcherOwner,
                )
            }
    }
}
