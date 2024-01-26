package com.bumble.appyx.core.node

import android.os.Parcelable
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.hasTestTag
import com.bumble.appyx.core.AppyxTestScenario
import com.bumble.appyx.core.children.nodeOrNull
import com.bumble.appyx.core.composable.PermanentChild
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.navigation.model.permanent.PermanentNavModel
import kotlinx.parcelize.Parcelize
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
        createPermanentNavModelWithNavKey()
        rule.start()

        rule.onNode(hasTestTag(TestParentNode.NavTarget::class.java.name)).assertExists()
    }

    @Test
    fun `WHEN_permanent_model_does_not_contain_relevant_nav_key_THEN_permanent_child_is_not_rendered`() {
        rule.start()

        rule.onNode(hasTestTag(TestParentNode.NavTarget::class.java.name)).assertDoesNotExist()
    }

    @Test
    fun `WHEN_visibility_switched_THEN_permanent_child_is_reused`() {
        createPermanentNavModelWithNavKey()
        rule.start()
        rule.node.renderPermanentChild = false
        val childNodes = rule.node.children.value.values.map { it.nodeOrNull }

        rule.onNode(hasTestTag(TestParentNode.NavTarget::class.java.name)).assertDoesNotExist()

        rule.node.renderPermanentChild = true

        rule.onNode(hasTestTag(TestParentNode.NavTarget::class.java.name)).assertExists()
        assertEquals(childNodes, rule.node.children.value.values.map { it.nodeOrNull })
    }

    private fun createPermanentNavModelWithNavKey() {
        nodeFactory = {
            TestParentNode(
                buildContext = it,
                permanentNavModel = PermanentNavModel(
                    TestParentNode.NavTarget,
                    savedStateMap = null,
                )
            )
        }

    }

    class TestParentNode(
        buildContext: BuildContext,
        private val permanentNavModel: PermanentNavModel<NavTarget> = PermanentNavModel(
            savedStateMap = buildContext.savedStateMap
        ),
    ) : ParentNode<TestParentNode.NavTarget>(
        buildContext = buildContext,
        navModel = permanentNavModel
    ) {

        @Parcelize
        object NavTarget : Parcelable

        var renderPermanentChild by mutableStateOf(true)

        override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
            node(buildContext) { modifier ->
                BasicText(
                    text = navTarget.toString(),
                    modifier = modifier.testTag(NavTarget::class.java.name),
                )
            }

        @Composable
        override fun View(modifier: Modifier) {
            if (renderPermanentChild) {
                PermanentChild(permanentNavModel, NavTarget)
            }
        }
    }

}
