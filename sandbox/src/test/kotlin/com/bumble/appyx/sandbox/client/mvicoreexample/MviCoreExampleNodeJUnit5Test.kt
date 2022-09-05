package com.bumble.appyx.sandbox.client.mvicoreexample

import androidx.lifecycle.Lifecycle
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.firstChildOfType
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreExampleNode.Routing
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreExampleViewImpl.Event
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.MviCoreExampleFeature.News
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.MviCoreExampleFeature.State
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.MviCoreExampleFeature.Wish
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.ViewModel
import com.bumble.appyx.sandbox.stub.FeatureStub
import com.bumble.appyx.sandbox.stub.NodeViewStub
import com.bumble.appyx.testing.junit5.util.CoroutinesTestExtension
import com.bumble.appyx.testing.junit5.util.InstantExecutorExtension
import com.bumble.appyx.testing.unit.common.helper.ParentNodeTestHelper
import com.bumble.appyx.testing.unit.common.helper.parentNodeTestHelper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantExecutorExtension::class, CoroutinesTestExtension::class)
class MviCoreExampleNodeJUnit5Test {

    private val view = object : NodeViewStub<Event, ViewModel, Routing>(), MviCoreExampleView {}

    private val backStack = BackStack<Routing>(
        initialElement = Routing.Child1,
        savedStateMap = null
    )

    private val feature = FeatureStub<Wish, State, News>(
        initialState = State.InitialState("Test Initial State")
    )

    private val interactor = MviCoreExampleInteractor(
        view = view,
        feature = feature,
        backStack = backStack
    )


    private lateinit var node: MviCoreExampleNode
    private lateinit var testHelper: ParentNodeTestHelper<Routing, ParentNode<Routing>>

    @BeforeEach
    fun setUp() {
        node = MviCoreExampleNode(
            view = view,
            buildContext = BuildContext.root(savedStateMap = null),
            plugins = listOf(interactor),
            backStack = backStack,
        )

        testHelper = node.parentNodeTestHelper()
    }

    @Test
    fun `given node is created then first child is attached`() {
        testHelper.assertChildHasLifecycle(
            routing = Routing.Child1,
            state = Lifecycle.State.CREATED,
        )
    }

    @Test
    fun `given node is create when navigating to child 2 then child 2 is attached`() {
        testHelper.moveToStateAndCheck(Lifecycle.State.STARTED) {
            view.eventsRelay.accept(Event.SwitchChildClicked)

            testHelper.assertChildHasLifecycle(
                routing = Routing.Child2,
                state = Lifecycle.State.STARTED
            )
        }
    }

    @Test
    fun `when load data clicked then load data wish is received`() {
        val testObserver = feature.wishesRelay.test()
        testHelper.moveToStateAndCheck(Lifecycle.State.STARTED) {
            view.eventsRelay.accept(Event.LoadDataClicked)

            testObserver.assertValue(Wish.LoadData)
        }
    }

    @Test
    fun `given child 1 is set when child 1 outputs a result then wish is received`() {
        val testObserver = feature.wishesRelay.test()

        testHelper.moveToStateAndCheck(Lifecycle.State.STARTED) {
            node.firstChildOfType<MviCoreChildNode1>().output.accept(
                MviCoreChildNode1.Output.Result("hello")
            )

            testObserver.assertValue(Wish.ChildInput("hello"))
        }
    }

    @Test
    fun `given feature sends loading then view model is loading`() {
        val testObserver = view.viewModelRelay.test()

        testHelper.moveToStateAndCheck(Lifecycle.State.STARTED) {
            feature.statesRelay.accept(State.Loading)

            testObserver.assertValueSequence(
                listOf(
                    ViewModel.InitialState("Test Initial State"),
                    ViewModel.Loading
                )
            )
        }
    }

}
