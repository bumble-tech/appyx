package com.bumble.appyx.sandbox.client.mvicoreexample.leaf

import androidx.lifecycle.Lifecycle
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreExampleNode.Routing
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreExampleViewImpl.Event
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.MviCoreExampleFeature.News
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.MviCoreExampleFeature.State
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.MviCoreExampleFeature.Wish
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.ViewModel
import com.bumble.appyx.sandbox.stub.FeatureStub
import com.bumble.appyx.testing.unit.common.helper.interactorTestHelper
import com.bumble.appyx.sandbox.stub.NodeViewStub
import com.bumble.appyx.testing.junit5.util.CoroutinesTestExtension
import com.bumble.appyx.testing.junit5.util.InstantExecutorExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantExecutorExtension::class, CoroutinesTestExtension::class)
class MviCoreLeafInteractorJUnit5Test {

    private val view = object : NodeViewStub<Event, ViewModel, Routing>(), MviCoreLeafView {}

    private val feature = FeatureStub<Wish, State, News>(
        initialState = State.InitialState("Test Initial State")
    )

    private val interactor = MviCoreLeafInteractor(
        view = view,
        feature = feature
    )

    @Test
    fun `when load data clicked then load data wish is received`() {
        val testObserver = feature.wishesRelay.test()
        interactor.interactorTestHelper().moveToStateAndCheck(Lifecycle.State.STARTED) {
            view.eventsRelay.accept(Event.LoadDataClicked)

            testObserver.assertValue(Wish.LoadData)
        }
    }
}
