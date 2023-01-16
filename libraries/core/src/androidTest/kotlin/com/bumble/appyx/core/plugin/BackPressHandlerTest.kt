package com.bumble.appyx.core.plugin

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
import androidx.lifecycle.Lifecycle.State
import androidx.test.espresso.Espresso
import androidx.test.platform.app.InstrumentationRegistry
import com.bumble.appyx.Appyx
import com.bumble.appyx.core.AppyxTestScenario
import com.bumble.appyx.core.InternalAppyxTestActivity
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
        InternalAppyxTestActivity.reset()
    }

    @Test
    fun navigation_handles_back_press_when_plugin_has_disabled_listener() {
        rule.start()
        pushChildB()
        disablePlugin()

        Espresso.pressBack()
        Espresso.onIdle()
        rule.waitForIdle()

        assertThat(rule.node.backStack.activeElement, equalTo(TestParentNode.NavTarget.ChildA))
        assertThat(backHandler.onBackPressedHandled, equalTo(false))
    }

    @Test
    fun custom_plugin_handles_back_press_before_navigation() {
        rule.start()
        pushChildB()

        Espresso.pressBack()
        Espresso.onIdle()
        rule.waitForIdle()

        assertThat(rule.node.backStack.activeElement, equalTo(TestParentNode.NavTarget.ChildB))
        assertThat(backHandler.onBackPressedHandled, equalTo(true))
    }

    @Test
    fun activity_is_closed_when_nobody_can_handle_back_press() {
        rule.start()
        disablePlugin()

        Espresso.pressBackUnconditionally()
        Espresso.onIdle()
        rule.waitForIdle()

        assertThat(rule.activityScenario.state, equalTo(State.DESTROYED))
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
        runOnMainSync { rule.node.backStack.push(TestParentNode.NavTarget.ChildWithPlugin) }

        Espresso.pressBack()
        Espresso.onIdle()
        rule.waitForIdle()

        assertThat(childBackHandler.onBackPressedHandled, equalTo(true))
    }

    @Test
    fun appyx_handles_back_press_before_activity_handler() {
        InternalAppyxTestActivity.handleBackPress.value = true
        rule.start()
        pushChildB()

        Espresso.pressBack()
        Espresso.onIdle()
        rule.waitForIdle()

        assertThat(backHandler.onBackPressedHandled, equalTo(true))
    }

    @Test
    fun activity_handles_back_press_if_appyx_cant() {
        InternalAppyxTestActivity.handleBackPress.value = true
        rule.start()
        disablePlugin()

        Espresso.pressBack()
        Espresso.onIdle()
        rule.waitForIdle()

        assertThat(InternalAppyxTestActivity.onBackPressedHandled, equalTo(true))
    }

    @Test
    // real case for https://github.com/bumble-tech/appyx/issues/118
    fun appyx_handles_back_press_after_activity_returns_from_background() {
        fun TestParentNode.findChildNode() =
            children.value.values.firstNotNullOf { value ->
                value.nodeOrNull?.takeIf { value.key.navTarget == TestParentNode.NavTarget.ChildWithPlugin }
            } as TestParentNode

        rule.start()
        runOnMainSync {
            rule.node.run {
                backStack.push(TestParentNode.NavTarget.ChildWithPlugin)
                val node = findChildNode()
                node.backStack.push(TestParentNode.NavTarget.ChildB)
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
        runOnMainSync { rule.node.backStack.push(TestParentNode.NavTarget.ChildB) }
    }

    private fun disablePlugin() {
        backHandler.onBackPressedCallback.isEnabled = false
    }

    class TestParentNode(
        buildContext: BuildContext,
        val backStack: BackStack<NavTarget> = BackStack(
            initialElement = NavTarget.ChildA,
            savedStateMap = null,
        ),
        plugin: Plugin?,
        private val childPlugin: Plugin?,
    ) : ParentNode<TestParentNode.NavTarget>(
        buildContext = buildContext,
        navModel = backStack,
        plugins = listOfNotNull(plugin),
    ) {

        sealed class NavTarget : Parcelable {

            @Parcelize
            object ChildA : NavTarget()

            @Parcelize
            object ChildB : NavTarget()

            @Parcelize
            object ChildWithPlugin : NavTarget()

        }

        override fun resolve(navTarget: NavTarget, buildContext: BuildContext) = when (navTarget) {
            NavTarget.ChildA -> node(buildContext) {
                Spacer(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Green)
                )
            }
            NavTarget.ChildB -> node(buildContext) {
                Spacer(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Red)
                )
            }
            NavTarget.ChildWithPlugin -> TestParentNode(
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
