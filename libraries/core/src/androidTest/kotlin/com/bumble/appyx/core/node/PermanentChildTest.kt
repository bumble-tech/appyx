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
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.navigation.EmptyNavModel
import com.bumble.appyx.core.navigation.model.permanent.PermanentNavModel
import com.bumble.appyx.core.node.PermanentChildTest.TestParentNode.NavTarget
import kotlinx.parcelize.Parcelize
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class PermanentChildTest {

    var nodeFactory: (buildContext: BuildContext) -> TestParentNode = {
        TestParentNode(null, it)
    }

    @get:Rule
    val rule = AppyxTestScenario { buildContext ->
        nodeFactory(buildContext)
    }

    @Test
    fun permanent_child_is_rendered() {
        rule.start()

        rule.onNode(hasTestTag(NavTarget::class.java.name)).assertExists()
    }

    @Test
    fun permanent_child_is_reused_when_visibility_switched() {
        rule.start()
        rule.node.renderPermanentChild = false
        val childNodes = rule.node.children.value.values.map { it.nodeOrNull }

        rule.onNode(hasTestTag(NavTarget::class.java.name)).assertDoesNotExist()

        rule.node.renderPermanentChild = true

        rule.onNode(hasTestTag(NavTarget::class.java.name)).assertExists()
        assertEquals(childNodes, rule.node.children.value.values.map { it.nodeOrNull })
    }

    @Test
    fun given_permanent_model_with_key_When_PermanentChild_with_the_same_key_Then_has_one_child() {
        val permanentNavModel = PermanentNavModel<NavTarget>(NavTarget.Child1, savedStateMap = null)
        nodeFactory = { buildContext ->
            TestParentNode(permanentNavModel, buildContext)
        }

        rule.start()

        val childNodes = rule.node.children.value.values.map { it.nodeOrNull }
        assertTrue(childNodes.count() == 1)
    }

    @Test
    fun given_permanent_model_with_key_When_PermanentChild_add_new_key_Then_has_two_children() {
        val permanentNavModel = PermanentNavModel<NavTarget>(NavTarget.Child2, savedStateMap = null)
        nodeFactory = { buildContext ->
            TestParentNode(permanentNavModel, buildContext)
        }

        rule.start()

        val childNodes = rule.node.children.value.values.map { it.nodeOrNull }
        assertTrue(childNodes.count() == 2)
    }

    class TestParentNode(
        private val permanentNavModel: PermanentNavModel<NavTarget>? = null,
        buildContext: BuildContext,
    ) : ParentNode<NavTarget>(
        buildContext = buildContext,
        navModel = permanentNavModel ?: EmptyNavModel<NavTarget, Any>(),
    ) {

        sealed class NavTarget : Parcelable {
            @Parcelize
            object Child1 : NavTarget()

            @Parcelize
            object Child2 : NavTarget()
        }

        var renderPermanentChild by mutableStateOf(true)


        class ChildNode(
            private val navTarget: NavTarget,
            buildContext: BuildContext
        ) : Node(buildContext) {

            @Composable
            override fun View(modifier: Modifier) {
                BasicText(
                    text = navTarget.toString(),
                    modifier = modifier.testTag(NavTarget::class.java.name),
                )
            }
        }

        override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
            ChildNode(navTarget, buildContext)

        @Composable
        override fun View(modifier: Modifier) {
            if (renderPermanentChild) {
                PermanentChild(NavTarget.Child1)
            }
        }
    }

}
