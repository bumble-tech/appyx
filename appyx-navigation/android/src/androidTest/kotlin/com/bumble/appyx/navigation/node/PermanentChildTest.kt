package com.bumble.appyx.navigation.node

import android.os.Parcelable
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.hasTestTag
import com.bumble.appyx.interactions.core.model.EmptyInteractionModel
import com.bumble.appyx.navigation.AppyxTestScenario
import com.bumble.appyx.navigation.children.nodeOrNull
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.PermanentChildTest.TestParentNode.InteractionTarget
import kotlinx.parcelize.Parcelize
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class PermanentChildTest {

    @get:Rule
    val rule = AppyxTestScenario { buildContext ->
        TestParentNode(buildContext)
    }

    @Test
    fun permanent_child_is_rendered() {
        rule.start()

        rule.onNode(hasTestTag(InteractionTarget::class.java.name)).assertExists()
    }

    @Test
    fun permanent_child_is_reused_when_visibility_switched() {
        rule.start()
        rule.node.renderPermanentChild = false
        val childNodes = rule.node.children.value.values.map { it.nodeOrNull }

        rule.onNode(hasTestTag(InteractionTarget::class.java.name)).assertDoesNotExist()

        rule.node.renderPermanentChild = true

        rule.onNode(hasTestTag(InteractionTarget::class.java.name)).assertExists()
        assertEquals(childNodes, rule.node.children.value.values.map { it.nodeOrNull })
    }

    class TestParentNode(
        buildContext: BuildContext,
    ) : ParentNode<InteractionTarget>(
        buildContext = buildContext,
        interactionModel = EmptyInteractionModel(),
    ) {

        @Parcelize
        object InteractionTarget : Parcelable

        var renderPermanentChild by mutableStateOf(true)

        override fun resolve(interactionTarget: InteractionTarget, buildContext: BuildContext): Node =
            node(buildContext) { modifier ->
                BasicText(
                    text = interactionTarget.toString(),
                    modifier = modifier.testTag(InteractionTarget::class.java.name),
                )
            }

        @Composable
        override fun View(modifier: Modifier) {
            if (renderPermanentChild) {
                PermanentChild(InteractionTarget)
            }
        }
    }

}
