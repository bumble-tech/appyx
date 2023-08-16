package com.bumble.appyx.utils.interop.ribs

import android.os.Bundle
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.core.os.bundleOf
import androidx.lifecycle.LifecycleEventObserver
import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.core.view.ViewFactory
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.platform.toCommonState
import com.bumble.appyx.utils.customisations.NodeCustomisation
import com.bumble.appyx.utils.interop.ribs.InteropNode.Customisation
import com.bumble.appyx.utils.interop.ribs.InteropViewImpl.Factory

interface InteropNode<N : Node> : Rib {
    val appyxNode: N

    class Customisation(
        val viewFactory: ViewFactory<InteropView>
    ) : NodeCustomisation
}

internal class InteropNodeImpl<N : Node>(
    buildParams: BuildParams<*>,
    override val appyxNode: N,
    private val backPressHandler: InteropBackPressHandler = InteropBackPressHandler(),
) : com.badoo.ribs.core.Node<InteropView>(
    buildParams = buildParams,
    viewFactory = buildParams.getOrDefault(
        Customisation(
            viewFactory = Factory<N>().invoke(
                object : InteropView.Dependency<N> {
                    override val appyxNode: N = appyxNode
                    override val onBackPressedDispatcherOwner: OnBackPressedDispatcherOwner =
                        backPressHandler
                }
            )
        ),
    ).viewFactory,
    plugins = listOf(backPressHandler),
), InteropNode<N> {

    private val observer = LifecycleEventObserver { source, _ ->
        appyxNode.updateLifecycleState(source.lifecycle.currentState.toCommonState())
    }

    override fun onCreate() {
        super.onCreate()
        appyxNode.updateLifecycleState(lifecycle.currentState.toCommonState())
        lifecycle.addObserver(observer)
    }

    override fun onDestroy(isRecreating: Boolean) {
        super.onDestroy(isRecreating)
        lifecycle.removeObserver(observer)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val state = appyxNode.saveInstanceState { true }
        outState.putBundle(InteropNodeKey, state.toBundle())
    }

    companion object {
        const val InteropNodeKey = "InteropNodeKey"
    }

    @Suppress("SpreadOperator")
    private fun Map<String, Any?>.toBundle(): Bundle = bundleOf(*this.toList().toTypedArray())

}
