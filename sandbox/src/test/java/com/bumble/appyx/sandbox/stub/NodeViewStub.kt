package com.bumble.appyx.sandbox.stub

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.node.EmptyNodeView
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.ParentNodeView
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.ObservableSource
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.functions.Consumer

open class NodeViewStub<Event : Any, ViewModel : Any, Routing : Any>(
    val eventsRelay: PublishRelay<Event> = PublishRelay.create(),
    val viewModelRelay: PublishRelay<ViewModel> = PublishRelay.create(),
    private val disposable: Disposable = Disposables.empty()
) : ParentNodeView<Routing>(),
    ObservableSource<Event> by eventsRelay,
    Consumer<ViewModel> by viewModelRelay,
    Disposable by disposable {

    @Composable
    override fun ParentNode<Routing>.NodeView(modifier: Modifier) {
        EmptyNodeView
    }
}
