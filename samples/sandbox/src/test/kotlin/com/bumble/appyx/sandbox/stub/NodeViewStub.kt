package com.bumble.appyx.sandbox.stub

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.ParentNodeView
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.ObservableSource
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.functions.Consumer

open class NodeViewStub<Event : Any, ViewModel : Any, NavTarget : Parcelable>(
    val eventsRelay: PublishRelay<Event> = PublishRelay.create(),
    val viewModelRelay: PublishRelay<ViewModel> = PublishRelay.create(),
    private val disposable: Disposable = Disposables.empty()
) : ParentNodeView<NavTarget>,
    ObservableSource<Event> by eventsRelay,
    Consumer<ViewModel> by viewModelRelay,
    Disposable by disposable {

    @Composable
    override fun ParentNode<NavTarget>.NodeView(modifier: Modifier) = Unit
}
