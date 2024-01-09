package com.bumble.appyx.navigation.node

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.hasTestTag
import com.bumble.appyx.interactions.permanent.PermanentAppyxComponent
import com.bumble.appyx.navigation.AppyxTestScenario
import com.bumble.appyx.navigation.children.nodeOrNull
import com.bumble.appyx.navigation.composable.PermanentChild
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.PermanentChildTest.TestParentNode.ChildReference
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class PermanentChildTest {

    var nodeFactory: (buildContext: BuildContext) -> TestParentNode = {
        TestParentNode(buildContext = it)
    }

    @get:Rule
    val rule = AppyxTestScenario { buildContext ->
        nodeFactory(buildContext)
    }

    @Test
    fun `WHEN_permanent_model_contains_relevant_nav_key_THEN_permanent_child_is_rendered`() {
        createPermanentAppyxComponentWithInteractionKey()
        rule.start()

        rule.onNode(hasTestTag(ChildReference::class.java.name)).assertExists()
    }

    @Test
    fun `WHEN_permanent_model_does_not_contain_relevant_nav_key_THEN_permanent_child_is_not_rendered`() {
        rule.start()

        rule.onNode(hasTestTag(ChildReference::class.java.name))
            .assertDoesNotExist()
    }

    @Test
    fun `WHEN_visibility_switched_THEN_permanent_child_is_reused`() {
        createPermanentAppyxComponentWithInteractionKey()
        rule.start()
        rule.node.renderPermanentChild = false
        val childNodes = rule.node.children.value.values.map { it.nodeOrNull }

        rule.onNode(hasTestTag(ChildReference::class.java.name))
            .assertDoesNotExist()

        rule.node.renderPermanentChild = true

        rule.onNode(hasTestTag(ChildReference::class.java.name)).assertExists()
        assertEquals(childNodes, rule.node.children.value.values.map { it.nodeOrNull })
    }

    private fun createPermanentAppyxComponentWithInteractionKey() {
        nodeFactory = {
            TestParentNode(
                buildContext = it,
                permanentAppyxComponent = PermanentAppyxComponent(
                    savedStateMap = null,
                    listOf(ChildReference)
                )
            )
        }

    }

    class TestParentNode(
        buildContext: BuildContext,
        private val permanentAppyxComponent: PermanentAppyxComponent<ChildReference> =
            PermanentAppyxComponent(savedStateMap = buildContext.savedStateMap)
    ) : ParentNode<ChildReference>(
        buildContext = buildContext,
        appyxComponent = permanentAppyxComponent
    ) {

        @Parcelize
        object ChildReference : Parcelable

        var renderPermanentChild by mutableStateOf(true)

        override fun buildChildNode(
            reference: ChildReference,
            buildContext: BuildContext
        ): Node =
            node(buildContext) { modifier ->
                BasicText(
                    text = reference.toString(),
                    modifier = modifier.testTag(ChildReference::class.java.name),
                )
            }

        @Composable
        override fun View(modifier: Modifier) {
            if (renderPermanentChild) {
                PermanentChild(permanentAppyxComponent, ChildReference)
            }
        }
    }

}
