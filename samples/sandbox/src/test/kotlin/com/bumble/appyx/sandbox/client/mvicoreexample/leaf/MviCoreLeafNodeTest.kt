package com.bumble.appyx.sandbox.client.mvicoreexample.leaf

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.interop.rx2.plugin.disposeOnDestroyPlugin
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreExampleNode.NavTarget
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreExampleViewImpl.Event
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.MviCoreExampleFeature.News
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.MviCoreExampleFeature.State
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.MviCoreExampleFeature.Wish
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.ViewModel
import com.bumble.appyx.sandbox.stub.FeatureStub
import com.bumble.appyx.sandbox.stub.NodeViewStub
import com.bumble.appyx.testing.junit4.util.MainDispatcherRule
import com.bumble.appyx.testing.unit.common.helper.NodeTestHelper
import com.bumble.appyx.testing.unit.common.helper.nodeTestHelper
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MviCoreLeafNodeTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val view = object : NodeViewStub<Event, ViewModel, NavTarget>(), MviCoreLeafView {}

    private val stateName = "Test Initial State"
    private val feature = FeatureStub<Wish, State, News>(
        initialState = State.InitialState(stateName)
    )

    private val interactor = MviCoreLeafInteractor(
        view = view,
        feature = feature
    )

    private lateinit var node: MviCoreLeafNode
    private lateinit var testHelper: NodeTestHelper<Node>

    @Before
    fun setUp() {
        node = MviCoreLeafNode(
            buildContext = BuildContext.root(savedStateMap = null),
            view = view,
            plugins = listOf(interactor, disposeOnDestroyPlugin(feature)),
        )

        testHelper = node.nodeTestHelper()
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
    fun `when state is updated then updated view model is received`() {
        val testObserver = view.viewModelRelay.test()
        testHelper.moveToStateAndCheck(Lifecycle.State.STARTED) {
            feature.statesRelay.accept(State.Loading)

            testObserver.assertValues(ViewModel.InitialState(stateName), ViewModel.Loading)
        }
    }

    @Test
    fun `when state is destroyed then feature is disposed`() {
        testHelper.moveTo(Lifecycle.State.DESTROYED)
        assertTrue(feature.isDisposed)
    }
}
