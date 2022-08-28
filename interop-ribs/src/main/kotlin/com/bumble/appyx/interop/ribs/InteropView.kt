package com.bumble.appyx.interop.ribs

import android.content.Context
import androidx.compose.runtime.Composable
import com.badoo.ribs.compose.ComposeRibView
import com.badoo.ribs.compose.ComposeView
import com.badoo.ribs.core.view.RibView
import com.badoo.ribs.core.view.ViewFactory
import com.badoo.ribs.core.view.ViewFactoryBuilder
import com.bumble.appyx.core.integrationpoint.LocalIntegrationPoint
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.interop.ribs.InteropView.Dependency

interface InteropView : RibView {

    interface Dependency<N : Node> {
        val appyxNode: N
    }
}

internal class InteropViewImpl private constructor(
    override val context: Context,
    private val appyxNode: Node,
) : InteropView, ComposeRibView(context) {

    override val composable: ComposeView
        get() = @Composable {
            appyxNode.integrationPoint = LocalIntegrationPoint.current
            appyxNode.Compose()
        }

    class Factory<N : Node> : ViewFactoryBuilder<Dependency<N>, InteropView> {
        override fun invoke(deps: Dependency<N>): ViewFactory<InteropView> =
            ViewFactory {
                InteropViewImpl(
                    context = it.parent.context,
                    appyxNode = deps.appyxNode
                )
            }
    }
}
