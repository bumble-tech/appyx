package com.bumble.appyx.v2.sandbox.client.interop

import androidx.lifecycle.Lifecycle
import com.badoo.mvicore.android.lifecycle.createDestroy
import com.badoo.ribs.clienthelper.interactor.Interactor
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.routing.source.backstack.BackStack
import com.badoo.ribs.routing.source.backstack.operation.push
import com.bumble.appyx.v2.sandbox.client.interop.V1Router.Configuration
import com.bumble.appyx.v2.sandbox.client.interop.V1View.Event
import io.reactivex.functions.Consumer

internal class V1Interactor(
    private val backStack: BackStack<Configuration>,
    buildParams: BuildParams<*>
) : Interactor<V1Rib, V1View>(
    buildParams = buildParams
) {

    override fun onViewCreated(view: V1View, viewLifecycle: Lifecycle) {
        viewLifecycle.createDestroy {
            bind(view to Consumer<Event> {
                val current = backStack.state.current!!.routing.configuration
                if (current is Configuration.Content.Main) {
                    backStack.push(Configuration.Content.Empty)
                } else {
                    backStack.push(Configuration.Content.Main)
                }
            })
        }
    }
}
