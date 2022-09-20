package com.bumble.appyx.sandbox.client.test

import com.bumble.appyx.testing.ui.rules.AppyxParentViewTestRule
import com.bumble.appyx.core.node.ParentNodeView
import com.bumble.appyx.core.node.ViewFactory
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer
import io.reactivex.observers.TestObserver

class AppyxMviParentViewTestRule<NavTarget : Any, ViewModel : Any, Event : Any, View : ParentNodeView<NavTarget>>(
    launchActivity: Boolean = true,
    private val modelConsumer: (View) -> Consumer<in ViewModel>,
    private val eventObservable: (View) -> ObservableSource<out Event>,
    viewFactory: ViewFactory<View>,
) : AppyxParentViewTestRule<NavTarget, View>(
    launchActivity = launchActivity,
    viewFactory = viewFactory,
) {
    private lateinit var _modelConsumer: Consumer<in ViewModel>

    val events: List<Event>
        get() = testEvents.values()
    val testEvents: TestObserver<Event> = TestObserver()

    override fun beforeActivityLaunched() {
        super.beforeActivityLaunched()
        runOnUiThread {
            _modelConsumer = modelConsumer(view)
            eventObservable(view).wrapToObservable().subscribe(testEvents)
        }
    }

    override fun after() {
        super.after()
        testEvents.dispose()
    }

    fun accept(v: ViewModel) {
        runOnUiThread { _modelConsumer.accept(v) }
    }
}
