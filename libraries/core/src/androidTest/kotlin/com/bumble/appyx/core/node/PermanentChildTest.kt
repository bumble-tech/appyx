package com.bumble.appyx.core.node

import android.os.Parcelable
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onRoot
import androidx.test.core.graphics.writeToTestStorage
import com.bumble.appyx.core.AppyxTestScenario
import com.bumble.appyx.core.children.nodeOrNull
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.navigation.EmptyNavModel
import kotlinx.parcelize.Parcelize
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName

class PermanentChildTest {

    @get:Rule
    val nameRule = TestName()

    @get:Rule
    val rule = AppyxTestScenario { buildContext ->
        TestParentNode(buildContext)
    }

    @Test
    fun permanent_child_is_rendered() {
        rule.start()

        rule.onNode(hasTestTag(TestParentNode.NavTarget::class.java.name)).assertExists()

        rule.onRoot().captureToImage().asAndroidBitmap()
            .writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}")
    }

    @Test
    fun permanent_child_is_reused_when_visibility_switched() {
        rule.start()
        rule.node.renderPermanentChild = false
        val childNodes = rule.node.children.value.values.map { it.nodeOrNull }

        rule.onNode(hasTestTag(TestParentNode.NavTarget::class.java.name)).assertDoesNotExist()

        rule.node.renderPermanentChild = true

        rule.onNode(hasTestTag(TestParentNode.NavTarget::class.java.name)).assertExists()
        assertEquals(childNodes, rule.node.children.value.values.map { it.nodeOrNull })
    }

    class TestParentNode(
        buildContext: BuildContext,
    ) : ParentNode<TestParentNode.NavTarget>(
        buildContext = buildContext,
        navModel = EmptyNavModel<NavTarget, Any>(),
    ) {

        @Parcelize
        object NavTarget : Parcelable

        var renderPermanentChild by mutableStateOf(true)

        override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
            node(buildContext) { modifier ->
                BasicText(
                    text = navTarget.toString() + "aa",
                    modifier = modifier.testTag(NavTarget::class.java.name),
                )
            }

        @Composable
        override fun View(modifier: Modifier) {
            if (renderPermanentChild) {
                PermanentChild(NavTarget)
            }
        }
    }

}
