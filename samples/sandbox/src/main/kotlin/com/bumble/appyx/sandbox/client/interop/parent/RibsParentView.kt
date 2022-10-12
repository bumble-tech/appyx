package com.bumble.appyx.sandbox.client.interop.parent

import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import com.badoo.ribs.core.Node
import com.badoo.ribs.core.customisation.inflate
import com.badoo.ribs.core.view.AndroidRibView
import com.badoo.ribs.core.view.RibView
import com.badoo.ribs.core.view.ViewFactory
import com.badoo.ribs.core.view.ViewFactoryBuilder
import com.bumble.appyx.R
import com.bumble.appyx.sandbox.client.interop.parent.RibsParentView.Event
import com.bumble.appyx.sandbox.client.interop.parent.RibsParentView.Event.SwitchClicked
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.ObservableSource

interface RibsParentView : RibView, ObservableSource<Event> {

    interface Factory : ViewFactoryBuilder<Nothing?, RibsParentView>

    sealed class Event {
        object SwitchClicked : Event()
    }
}

class RibsParentViewImpl private constructor(
    private val events: PublishRelay<Event> = PublishRelay.create(),
    override val androidView: ViewGroup
) : AndroidRibView(), RibsParentView, ObservableSource<Event> by events {

    private val container = androidView.findViewById<FrameLayout>(R.id.child)
    private val switch = androidView.findViewById<Button>(R.id.switchButton)

    init {
        switch.setOnClickListener {
            events.accept(SwitchClicked)
        }
    }

    class Factory : RibsParentView.Factory {
        override fun invoke(deps: Nothing?): ViewFactory<RibsParentView> =
            ViewFactory {
                val view = it.inflate<ViewGroup>(R.layout.rib_root)
                RibsParentViewImpl(androidView = view)
            }
    }

    override fun getParentViewForSubtree(subtreeOf: Node<*>): ViewGroup = container

}
