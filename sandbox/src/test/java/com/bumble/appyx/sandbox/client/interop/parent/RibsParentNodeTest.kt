package com.bumble.appyx.sandbox.client.interop.parent

import androidx.lifecycle.Lifecycle
import com.badoo.ribs.builder.Builder
import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.modality.BuildContext
import com.badoo.ribs.core.view.RibView
import com.badoo.ribs.core.view.ViewFactory
import com.badoo.ribs.test.builder.RibBuilderStub
import com.badoo.ribs.test.node.RibNodeStub
import com.badoo.ribs.test.node.RibNodeTestHelper
import com.badoo.ribs.test.rx2.view.RibViewStub
import com.bumble.appyx.core.builder.SimpleBuilder
import com.bumble.appyx.interop.ribs.InteropNode
import com.bumble.appyx.interop.ribs.InteropView
import com.bumble.appyx.sandbox.client.interop.parent.RibsParentView.Event
import com.bumble.appyx.sandbox.client.interop.parent.routing.RibsParentChildBuilders
import com.bumble.appyx.testing.junit5.util.CoroutinesTestExtension
import com.bumble.appyx.testing.junit5.util.InstantExecutorExtension
import com.bumble.appyx.testing.unit.common.util.InteropSimpleBuilderStub
import com.bumble.appyx.utils.customisations.NodeCustomisationDirectoryImpl
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantExecutorExtension::class, CoroutinesTestExtension::class)
internal class RibsParentNodeTest {

    private val interopNodeBuilder = InteropSimpleBuilderStub()
    private val ribsNodeBuilder = RibBuilderStub<Nothing?, Rib> {
        object : RibNodeStub<RibView>(it), Rib {}
    }
    private val view = object : RibViewStub<Nothing, Event>(), RibsParentView {}

    private lateinit var ribsParentNode: RibsParentRib
    private lateinit var nodeTestHelper: RibNodeTestHelper<RibsParentRib>

    @BeforeEach
    fun setup() {
        ribsParentNode = RibsParentBuilder(
            childBuilders = object : RibsParentChildBuilders {
                override val interopNode: SimpleBuilder = interopNodeBuilder
                override val ribsNode: Builder<Nothing?, Rib> = ribsNodeBuilder
            }
        ).build(
            buildContext = BuildContext.root(
                savedInstanceState = null,
                customisations = NodeCustomisationDirectoryImpl().apply {
                    put(RibsParentRib.Customisation(object : RibsParentView.Factory {
                        override fun invoke(p1: Nothing?): ViewFactory<RibsParentView> =
                            ViewFactory { view }
                    }))
                    put(InteropNode.Customisation(
                        { object : RibViewStub<Nothing, Nothing>(), InteropView {} }
                    ))
                }
            ),
            payload = null,
        )
        nodeTestHelper = RibNodeTestHelper(ribsParentNode)
    }

    @Test
    fun `WHEN created THEN interop node is created`() {
        nodeTestHelper.moveToStateAndCheck(Lifecycle.State.STARTED) {
            interopNodeBuilder.assertCreatedNode()
            interopNodeBuilder.assertLastNodeState(Lifecycle.State.STARTED)
        }
    }

    @Test
    fun `WHEN SwitchClicked view event THEN ribs node is created`() {
        nodeTestHelper.moveToStateAndCheck(Lifecycle.State.STARTED) {
            view.viewEventRelay.accept(Event.SwitchClicked)

            ribsNodeBuilder.assertCreatedNode()
            ribsNodeBuilder.assertLastNodeState(Lifecycle.State.STARTED)
        }
    }
}
