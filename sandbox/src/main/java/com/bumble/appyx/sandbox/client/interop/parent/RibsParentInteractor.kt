package com.bumble.appyx.sandbox.client.interop.parent

import androidx.lifecycle.Lifecycle
import com.badoo.mvicore.android.lifecycle.createDestroy
import com.badoo.ribs.clienthelper.interactor.Interactor
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.routing.source.backstack.BackStack
import com.badoo.ribs.routing.source.backstack.operation.push
import com.bumble.appyx.sandbox.client.interop.parent.RibsParentRouter.Configuration
import com.bumble.appyx.sandbox.client.interop.parent.RibsParentView.Event
import io.reactivex.functions.Consumer

internal class RibsParentInteractor(
    private val backStack: BackStack<Configuration>,
    buildParams: BuildParams<*>
) : Interactor<RibsParentRib, RibsParentView>(
    buildParams = buildParams
) {

    override fun onViewCreated(view: RibsParentView, viewLifecycle: Lifecycle) {
        viewLifecycle.createDestroy {
            bind(view to Consumer<Event> {
                val current = backStack.activeConfiguration
                if (current is Configuration.InteropNode) {
                    backStack.push(Configuration.RibsNode)
                } else {
                    backStack.push(Configuration.InteropNode)
                }
            })
        }
    }
}
