package com.bumble.appyx.core.plugin

import android.app.Activity
import androidx.activity.OnBackPressedCallback
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.test.espresso.Espresso
import androidx.test.platform.app.InstrumentationRegistry
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.node
import com.bumble.appyx.debug.Appyx
import com.bumble.appyx.routingsource.backstack.BackStack
import com.bumble.appyx.routingsource.backstack.activeRouting
import com.bumble.appyx.routingsource.backstack.operation.push
import com.bumble.appyx.testing.ui.rules.AppyxTestRule
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.instanceOf
import org.junit.After
import org.junit.Rule
import org.junit.Test

class BackPressHandlerTest {

    private var onBackPressedHandled = false
    private var backPressHandler: BackPressHandler = object : BackPressHandler {
        override val onBackPressedCallback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onBackPressedHandled = true
                }
            }
    }

    @get:Rule
    val rule = AppyxTestRule(launchActivity = false) { buildContext ->
        TestParentNode(buildContext = buildContext, plugin = backPressHandler)
    }

    @After
    fun after() {
        Appyx.exceptionHandler = null
    }

    @Test
    fun routing_handles_back_press_when_plugin_has_disabled_listener() {
        rule.start()
        runOnMainSync {
            backPressHandler.onBackPressedCallback!!.isEnabled = false
            rule.node.backStack.push(TestParentNode.Routing.ChildB)
        }

        Espresso.pressBack()
        Espresso.onIdle()

        assertThat(rule.node.backStack.activeRouting, equalTo(TestParentNode.Routing.ChildA))
        assertThat(onBackPressedHandled, equalTo(false))
    }

    @Test
    fun custom_plugin_handles_back_press_before_routing() {
        rule.start()
        runOnMainSync { rule.node.backStack.push(TestParentNode.Routing.ChildB) }

        Espresso.pressBack()
        Espresso.onIdle()

        assertThat(rule.node.backStack.activeRouting, equalTo(TestParentNode.Routing.ChildB))
        assertThat(onBackPressedHandled, equalTo(true))
    }

    @Test
    fun activity_is_closed_when_nobody_can_handle_back_press() {
        rule.start()
        runOnMainSync {
            backPressHandler.onBackPressedCallback!!.isEnabled = false
        }

        Espresso.pressBackUnconditionally()
        Espresso.onIdle()

        assertThat(rule.activityResult.resultCode, equalTo(Activity.RESULT_CANCELED))
    }

    @Test
    fun reports_incorrect_handler() {
        var exception: Exception? = null
        Appyx.exceptionHandler = { exception = it }
        backPressHandler = object : BackPressHandler {
            override val onBackPressedCallback: OnBackPressedCallback =
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                    }
                }

            override val onBackPressedCallbackList: List<OnBackPressedCallback> = listOf(
                onBackPressedCallback, object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                    }
                }
            )
        }
        rule.start()

        Espresso.onIdle()

        assertThat(exception, instanceOf(IllegalStateException::class.java))
    }

    private fun runOnMainSync(runner: Runnable) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(runner)
    }

    class TestParentNode(
        buildContext: BuildContext,
        val backStack: BackStack<Routing> = BackStack(
            initialElement = Routing.ChildA,
            savedStateMap = null,
        ),
        plugin: Plugin,
    ) : ParentNode<TestParentNode.Routing>(
        buildContext = buildContext,
        routingSource = backStack,
        plugins = listOf(plugin),
    ) {

        sealed class Routing {
            object ChildA : Routing()
            object ChildB : Routing()
        }

        override fun resolve(routing: Routing, buildContext: BuildContext) = when (routing) {
            Routing.ChildA -> node(buildContext) {}
            Routing.ChildB -> node(buildContext) {}
        }

        @Composable
        override fun View(modifier: Modifier) {
            Children(routingSource = backStack, modifier = modifier)
        }
    }

}
