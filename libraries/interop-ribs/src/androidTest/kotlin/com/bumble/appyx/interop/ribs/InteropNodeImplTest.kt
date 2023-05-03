package com.bumble.appyx.interop.ribs

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.espresso.Espresso
import org.junit.Rule
import org.junit.Test

class InteropNodeImplTest {

    @get:Rule
    val rule = createAndroidComposeRule(AppyxRibsInteropActivity::class.java)

    @Test
    fun appyx_back_press_is_handled_before_rib_parent() {
        // push a new child into the backstack
        var newChild = ""
        var oldChild = ""
        rule.activityRule.scenario.onActivity {
            oldChild = it.ribsNode.current()
            newChild = it.ribsNode.push()
        }

        Espresso.pressBack()

        // the back press is swollen by the child node, newChild is still displayed
        rule.onNodeWithTag(newChild).assertExists()

        Espresso.pressBack()

        // the back press is handled by RIBs now and the old child is displayed
        rule.onNodeWithTag(oldChild).assertExists()
    }

    @Test
    fun appyx_up_navigation_is_propagated_to_ribs() {
        var oldChild = ""
        rule.activityRule.scenario.onActivity {
            oldChild = it.ribsNode.current()
            it.ribsNode.push()
            it.ribsNode.navigateUpFromActiveAppyxChild()
        }

        // up navigation propagated to RIBs and it pops backstack
        rule.onNodeWithTag(oldChild).assertExists()
    }

}
