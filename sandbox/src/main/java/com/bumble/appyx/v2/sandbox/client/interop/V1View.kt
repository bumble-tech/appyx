package com.bumble.appyx.v2.sandbox.client.interop

import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import com.badoo.ribs.core.Node
import com.badoo.ribs.core.customisation.inflate
import com.badoo.ribs.core.view.AndroidRibView
import com.badoo.ribs.core.view.RibView
import com.badoo.ribs.core.view.ViewFactory
import com.badoo.ribs.core.view.ViewFactoryBuilder
import com.bumble.appyx.v2.R
import com.bumble.appyx.v2.sandbox.client.interop.V1View.Event
import com.bumble.appyx.v2.sandbox.client.interop.V1View.Event.Click
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.ObservableSource

interface V1View : RibView, ObservableSource<Event> {

    interface Factory : ViewFactoryBuilder<Nothing?, V1View>

    sealed class Event {
        object Click : Event()
    }
}

class V1ViewImpl private constructor(
    private val events: PublishRelay<Event> = PublishRelay.create(),
    override val androidView: ViewGroup
) : AndroidRibView(), V1View, ObservableSource<Event> by events {

    private val container = androidView.findViewById<FrameLayout>(R.id.child)
    private val switch = androidView.findViewById<Button>(R.id.swtich)

    init {
        switch.setOnClickListener {
            events.accept(Click)
        }
    }

    class Factory : V1View.Factory {
        override fun invoke(deps: Nothing?): ViewFactory<V1View> =
            ViewFactory {
                val view = it.inflate<ViewGroup>(R.layout.rib_root)
                V1ViewImpl(androidView = view)
            }
    }

    override fun getParentViewForSubtree(subtreeOf: Node<*>): ViewGroup = container

}
