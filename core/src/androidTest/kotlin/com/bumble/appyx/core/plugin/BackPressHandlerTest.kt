package com.bumble.appyx.core.plugin

import android.app.Activity
import android.os.Parcelable
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso
import androidx.test.platform.app.InstrumentationRegistry
import com.bumble.appyx.Appyx
import com.bumble.appyx.core.children.nodeOrNull
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.node
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.activeElement
import com.bumble.appyx.navmodel.backstack.operation.push
import kotlinx.parcelize.Parcelize
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.instanceOf
import org.junit.After
import org.junit.Rule
import org.junit.Test

class BackPressHandlerTest {

    private var backHandler = TestPlugin()
    private var childBackHandler = TestPlugin()

    @get:Rule
    val rule = AppyxTestScenario { buildContext ->
        TestParentNode(
            buildContext = buildContext,
            plugin = backHandler,
            childPlugin = childBackHandler,
        )
    }

    @After
    fun after() {
        Appyx.exceptionHandler = null
        BackPressHandlerTestActivity.reset()
    }

    @Test
    fun navigation_handles_back_press_when_plugin_has_disabled_listener() {
        rule.start()
        pushChildB()
        disablePlugin()

        Espresso.pressBack()
        Espresso.onIdle()
        rule.waitForIdle()

        assertThat(rule.node.backStack.activeElement, equalTo(TestParentNode.Routing.ChildA))
        assertThat(backHandler.onBackPressedHandled, equalTo(false))
    }

    @Test
    fun custom_plugin_handles_back_press_before_navigation() {
        rule.start()
        pushChildB()

        Espresso.pressBack()
        Espresso.onIdle()
        rule.waitForIdle()

        assertThat(rule.node.backStack.activeElement, equalTo(TestParentNode.Routing.ChildB))
        assertThat(backHandler.onBackPressedHandled, equalTo(true))
    }

    @Test
    fun activity_is_closed_when_nobody_can_handle_back_press() {
        rule.start()
        disablePlugin()

        Espresso.pressBackUnconditionally()
        Espresso.onIdle()
        rule.waitForIdle()

        assertThat(rule.activityScenario.result.resultCode, equalTo(Activity.RESULT_CANCELED))
    }

    @Test
    fun reports_incorrect_handler() {
        var exception: Exception? = null
        Appyx.exceptionHandler = { exception = it }
        backHandler = object : TestPlugin() {
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
        rule.waitForIdle()

        assertThat(exception, instanceOf(IllegalStateException::class.java))
    }

    @Test
    fun child_back_handler_works_before_parent() {
        rule.start()
        runOnMainSync { rule.node.backStack.push(TestParentNode.Routing.ChildWithPlugin) }

        Espresso.pressBack()
        Espresso.onIdle()
        rule.waitForIdle()

        assertThat(childBackHandler.onBackPressedHandled, equalTo(true))
    }

    @Test
    fun appyx_handles_back_press_before_activity_handler() {
        BackPressHandlerTestActivity.handleBackPress.value = true
        rule.start()
        pushChildB()

        Espresso.pressBack()
        Espresso.onIdle()
        rule.waitForIdle()

        assertThat(backHandler.onBackPressedHandled, equalTo(true))
    }

    @Test
    fun activity_handles_back_press_if_appyx_cant() {
        BackPressHandlerTestActivity.handleBackPress.value = true
        rule.start()
        disablePlugin()

        Espresso.pressBack()
        Espresso.onIdle()
        rule.waitForIdle()

        assertThat(BackPressHandlerTestActivity.onBackPressedHandled, equalTo(true))
    }

    @Test
    // real case for https://github.com/bumble-tech/appyx/issues/118
    fun appyx_handles_back_press_after_activity_returns_from_background() {
        fun TestParentNode.findChildNode() =
            children.value.values.firstNotNullOf { value ->
                value.nodeOrNull?.takeIf { value.key.navTarget == TestParentNode.Routing.ChildWithPlugin }
            } as TestParentNode

        rule.start()
        runOnMainSync {
            rule.node.run {
                backStack.push(TestParentNode.Routing.ChildWithPlugin)
                val node = findChildNode()
                node.backStack.push(TestParentNode.Routing.ChildB)
            }
        }

        rule.activityScenario.moveToState(Lifecycle.State.CREATED)
        rule.waitForIdle()
        rule.activityScenario.moveToState(Lifecycle.State.RESUMED)
        rule.waitForIdle()

        Espresso.pressBack()
        Espresso.onIdle()
        rule.waitForIdle()

        assertThat(childBackHandler.onBackPressedHandled, equalTo(true))
    }

    private fun runOnMainSync(runner: Runnable) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(runner)
    }

    private fun pushChildB() {
        runOnMainSync { rule.node.backStack.push(TestParentNode.Routing.ChildB) }
    }

    private fun disablePlugin() {
        backHandler.onBackPressedCallback.isEnabled = false
    }

    class TestParentNode(
        buildContext: BuildContext,
        val backStack: BackStack<Routing> = BackStack(
            initialElement = Routing.ChildA,
            savedStateMap = null,
        ),
        plugin: Plugin?,
        private val childPlugin: Plugin?,
    ) : ParentNode<TestParentNode.Routing>(
        buildContext = buildContext,
        navModel = backStack,
        plugins = listOfNotNull(plugin),
    ) {

        sealed class Routing : Parcelable {

            @Parcelize
            object ChildA : Routing()

            @Parcelize
            object ChildB : Routing()

            @Parcelize
            object ChildWithPlugin : Routing()

        }

        override fun resolve(navTarget: Routing, buildContext: BuildContext) = when (navTarget) {
            Routing.ChildA -> node(buildContext) {
                Spacer(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Green)
                )
            }
            Routing.ChildB -> node(buildContext) {
                Spacer(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Red)
                )
            }
            Routing.ChildWithPlugin -> TestParentNode(
                buildContext = buildContext,
                plugin = childPlugin,
                childPlugin = null,
            )
        }

        @Composable
        override fun View(modifier: Modifier) {
            Column(modifier) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black)
                        .height(26.dp)
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                )
                Children(navModel = backStack, modifier = Modifier.fillMaxSize())
            }
        }
    }

    private open class TestPlugin : BackPressHandler {
        var onBackPressedHandled = false
            private set
        override val onBackPressedCallback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onBackPressedHandled = true
                }
            }
    }

}
