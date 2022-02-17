package com.bumble.appyx.v2.sandbox.client.interop.parent

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
import com.bumble.appyx.v2.sandbox.client.interop.parent.V1ParentView.Event
import com.bumble.appyx.v2.sandbox.client.interop.parent.V1ParentView.Event.SwitchClicked
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.ObservableSource

interface V1ParentView : RibView, ObservableSource<Event> {

    interface Factory : ViewFactoryBuilder<Nothing?, V1ParentView>

    sealed class Event {
        object SwitchClicked : Event()
    }
}

class V1ParentViewImpl private constructor(
    private val events: PublishRelay<Event> = PublishRelay.create(),
    override val androidView: ViewGroup
) : AndroidRibView(), V1ParentView, ObservableSource<Event> by events {

    private val container = androidView.findViewById<FrameLayout>(R.id.child)
    private val switch = androidView.findViewById<Button>(R.id.switchButton)

    init {
        switch.setOnClickListener {
            events.accept(SwitchClicked)
        }
    }

    class Factory : V1ParentView.Factory {
        override fun invoke(deps: Nothing?): ViewFactory<V1ParentView> =
            ViewFactory {
                val view = it.inflate<ViewGroup>(R.layout.rib_root)
                V1ParentViewImpl(androidView = view)
            }
    }

    override fun getParentViewForSubtree(subtreeOf: Node<*>): ViewGroup = container

}
