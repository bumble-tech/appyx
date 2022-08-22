package com.bumble.appyx.sandbox.client.mvicoreexample

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import com.bumble.appyx.connectable.rx2.NodeConnector
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.plugin.Plugin
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreExampleNode.Routing
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreExampleViewImpl.Event
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.MviCoreExampleFeature.News
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.MviCoreExampleFeature.State
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.MviCoreExampleFeature.Wish
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.ViewModel
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.ViewModel.InitialState
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.ViewModel.Loading
import com.bumble.appyx.sandbox.stub.FeatureStub
import com.bumble.appyx.sandbox.stub.NodeViewStub
import com.bumble.appyx.testing.junit4.util.MainDispatcherRule
import com.bumble.appyx.testing.unit.common.helper.InteractorTestHelperWithConnectable
import com.bumble.appyx.testing.unit.common.helper.interactorTestHelperWithConnectable
import org.junit.Rule
import org.junit.Test
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreChildNode1.Input as Child1Input
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreChildNode1.Output as Child1Output
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreChildNode2.Input as Child2Input
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreChildNode2.Output as Child2Output
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreExampleNode.Input as ParentInput

class MviCoreExampleInteractorTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val view = object : NodeViewStub<Event, ViewModel, Routing>(), MviCoreExampleView {}
    private val feature = FeatureStub<Wish, State, News>(
        initialState = State.InitialState("Test Initial State")
    )
    private val parentNodeConnector: NodeConnector<ParentInput, Nothing> = NodeConnector()
    private val child1Connector: NodeConnector<Child1Input, Child1Output> = NodeConnector()
    private val child2Connector: NodeConnector<Child2Input, Child2Output> = NodeConnector()

    private lateinit var backStack: BackStack<Routing>
    private lateinit var interactorTestHelper: InteractorTestHelperWithConnectable<MviCoreExampleNode>

    private fun before(initialElement: Routing = Routing.Child1) {
        backStack = BackStack(
            initialElement = initialElement,
            savedStateMap = null,
        )

        val interactor = MviCoreExampleInteractor(
            view = view,
            feature = feature,
            backStack = backStack,
        )

        interactorTestHelper =
            interactor.interactorTestHelperWithConnectable(nodeBuilder = ::createNode)
    }

    @Test
    fun `given child 1 is set when input received then child 1 receives input`() {
        val child1InputTestObserver = child1Connector.input.test()
        val child2InputTestObserver = child2Connector.input.test()
        before()

        interactorTestHelper.moveToStateAndCheck(Lifecycle.State.STARTED) {
            parentNodeConnector.input.accept(ParentInput.ExampleInput)

            child1InputTestObserver.assertValue(Child1Input.ExampleInput1)
            child2InputTestObserver.assertNoValues()
        }
    }

    @Test
    fun `given child 2 is set when input received then child 2 receives input`() {
        val child1InputTestObserver = child1Connector.input.test()
        val child2InputTestObserver = child2Connector.input.test()
        before(initialElement = Routing.Child2)

        interactorTestHelper.moveToStateAndCheck(Lifecycle.State.STARTED) {
            parentNodeConnector.input.accept(ParentInput.ExampleInput)

            child2InputTestObserver.assertValue(Child2Input.ExampleInput2)
            child1InputTestObserver.assertNoValues()
        }
    }

    @Test
    fun `when load data clicked then load data wish is received`() {
        before()
        val testObserver = feature.wishesRelay.test()
        interactorTestHelper.moveToStateAndCheck(Lifecycle.State.STARTED) {
            view.eventsRelay.accept(Event.LoadDataClicked)

            testObserver.assertValue(Wish.LoadData)
        }
    }

    @Test
    fun `given child 1 is set when child 1 outputs a result then wish is received`() {
        before()
        val testObserver = feature.wishesRelay.test()

        child1Connector.output.accept(Child1Output.Result("hello"))

        testObserver.assertValue(Wish.ChildInput("hello"))
    }

    @Test
    fun `given child 2 is set when child 2 outputs a result then wish is received`() {
        before(initialElement = Routing.Child2)
        val testObserver = feature.wishesRelay.test()

        child2Connector.output.accept(Child2Output.Result("hello"))

        testObserver.assertValue(Wish.ChildInput("hello"))
    }

    @Test
    fun `given feature sends loading then view model is loading`() {
        before()
        val testObserver = view.viewModelRelay.test()

        interactorTestHelper.moveToStateAndCheck(Lifecycle.State.STARTED) {
            feature.statesRelay.accept(State.Loading)

            testObserver.assertValueSequence(
                listOf(
                    InitialState("Test Initial State"),
                    Loading
                )
            )
        }
    }

    private fun createNode(buildContext: BuildContext, plugins: List<Plugin>) = MviCoreExampleNode(
        view = view,
        buildContext = buildContext,
        plugins = plugins,
        backStack = backStack,
        connector = parentNodeConnector,
        child1Connector = child1Connector,
        child2Connector = child2Connector,
    )
}
