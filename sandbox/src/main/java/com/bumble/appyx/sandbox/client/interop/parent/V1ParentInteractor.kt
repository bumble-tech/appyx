package com.bumble.appyx.sandbox.client.interop.parent

import androidx.lifecycle.Lifecycle
import com.badoo.mvicore.android.lifecycle.createDestroy
import com.badoo.ribs.clienthelper.interactor.Interactor
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.routing.source.backstack.BackStack
import com.badoo.ribs.routing.source.backstack.operation.push
import com.bumble.appyx.sandbox.client.interop.parent.V1ParentRouter.Configuration
import com.bumble.appyx.sandbox.client.interop.parent.V1ParentView.Event
import io.reactivex.functions.Consumer

internal class V1ParentInteractor(
    private val backStack: BackStack<Configuration>,
    buildParams: BuildParams<*>
) : Interactor<V1ParentRib, V1ParentView>(
    buildParams = buildParams
) {

    override fun onViewCreated(view: V1ParentView, viewLifecycle: Lifecycle) {
        viewLifecycle.createDestroy {
            bind(view to Consumer<Event> {
                val current = backStack.activeConfiguration
                if (current is Configuration.InteropNode) {
                    backStack.push(Configuration.V1Node)
                } else {
                    backStack.push(Configuration.InteropNode)
                }
            })
        }
    }
}
