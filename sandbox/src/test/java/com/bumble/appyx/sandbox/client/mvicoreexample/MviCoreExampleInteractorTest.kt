package com.bumble.appyx.sandbox.client.mvicoreexample

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import com.bumble.appyx.connectable.rx2.NodeConnector
import com.bumble.appyx.routingsource.backstack.BackStack
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreExampleNode.Routing
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreExampleViewImpl.Event
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.MviCoreExampleFeature.News
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.MviCoreExampleFeature.State
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.MviCoreExampleFeature.Wish
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.ViewModel
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.ViewModel.InitialState
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.ViewModel.Loading
import com.bumble.appyx.sandbox.stub.FeatureStub
import com.bumble.appyx.testing.unit.common.helper.interactorTestHelper
import com.bumble.appyx.sandbox.stub.NodeViewStub
import com.bumble.appyx.testing.junit4.util.MainDispatcherRule
import org.junit.Rule
import org.junit.Test

class MviCoreExampleInteractorTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()


    private val view = object : NodeViewStub<Event, ViewModel, Routing>(), MviCoreExampleView {}

    private val feature = FeatureStub<Wish, State, News>(
        initialState = State.InitialState("Test Initial State")
    )

    private val backStack = BackStack<Routing>(
        initialElement = Routing.Child1,
        savedStateMap = null
    )

    private val interactor = MviCoreExampleInteractor(
        view = view,
        feature = feature,
        backStack = backStack
    )

    @Test
    fun `when load data clicked then load data wish is received`() {
        val testObserver = feature.wishesRelay.test()
        interactor.interactorTestHelper().moveToStateAndCheck(Lifecycle.State.STARTED) {
            view.eventsRelay.accept(Event.LoadDataClicked)

            testObserver.assertValue(Wish.LoadData)
        }
    }

    @Test
    fun `given child 1 is set when child 1 outputs a result then wish is received`() {
        val nodeConnector = NodeConnector<MviCoreChildNode1.Input, MviCoreChildNode1.Output>()
        val testObserver = feature.wishesRelay.test()

        interactor.interactorTestHelper(routingSource = backStack) {
            MviCoreChildNode1(
                buildContext = it,
                connector = nodeConnector
            )
        }

        nodeConnector.output.accept(MviCoreChildNode1.Output.Result("hello"))

        testObserver.assertValue(Wish.ChildInput("hello"))
    }

    @Test
    fun `given feature sends loading then view model is loading`() {
        val testObserver = view.viewModelRelay.test()

        interactor.interactorTestHelper().moveToStateAndCheck(Lifecycle.State.STARTED) {
            feature.statesRelay.accept(State.Loading)

            testObserver.assertValueSequence(
                listOf(
                    InitialState("Test Initial State"),
                    Loading
                )
            )
        }
    }
}
