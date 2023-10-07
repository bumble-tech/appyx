package com.bumble.appyx.interactions.ui.state

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.core.ui.LocalBoxScope
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.property.impl.position.BiasAlignment
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionAlignment
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.math.roundToInt

@OptIn(ExperimentalCoroutinesApi::class)
class MutableUiStateTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var testMutableUiState: TestMutableUiState
    private lateinit var coroutineScope: CoroutineScope

    private fun setupTestMutableUiState(
        target: PositionAlignment.Target = PositionAlignment.Target(
            insideAlignment = BiasAlignment.InsideAlignment.TopStart
        ),
        clipToBounds: Boolean = false,
        containerModifier: Modifier = Modifier
            .fillMaxSize(),
        childModifier: Modifier = Modifier,
    ) {
        composeTestRule.setContent {
            BoxWithConstraints(
                modifier = containerModifier
            ) {
                CompositionLocalProvider(LocalBoxScope provides this@BoxWithConstraints) {
                    val density = LocalDensity.current
                    val localConfiguration = LocalConfiguration.current
                    coroutineScope = rememberCoroutineScope()
                    val uiContext = remember { UiContext(coroutineScope, clipToBounds) }
                    testMutableUiState = remember {
                        TestMutableUiState(
                            uiContext = uiContext,
                            position = PositionAlignment(
                                coroutineScope = coroutineScope,
                                target = target,
                            )
                        ).apply {
                            updateBounds(
                                TransitionBounds(
                                    density = density,
                                    widthPx = this@BoxWithConstraints.constraints.maxWidth,
                                    heightPx = this@BoxWithConstraints.constraints.maxHeight,
                                    screenWidthPx = (localConfiguration.screenWidthDp * density.density).roundToInt(),
                                    screenHeightPx = (localConfiguration.screenHeightDp * density.density).roundToInt(),
                                )
                            )
                        }
                    }
                    Box(
                        modifier = childModifier
                            .then(testMutableUiState.visibilityModifier)
                    )
                }
            }
        }
    }

    @Test
    fun GIVEN_visible_state_WHEN_moved_outside_of_screen_THEN_visibility_is_false() = runTest {
        // child is in the top-left corner with the size of 60dp
        val childSize = 60.dp
        setupTestMutableUiState(
            childModifier = Modifier
                .requiredSize(childSize)
                .background(color = Color.Red)
        )

        // moving the child to the top-right corner + offset its size -> pushes it off screen
        testMutableUiState.snapTo(
            target = TestTargetUiState(
                position = PositionAlignment.Target(
                    insideAlignment = BiasAlignment.InsideAlignment.TopEnd,
                    offset = DpOffset(x = childSize, y = 0.dp)
                )
            )
        )

        // assert it's invisible
        composeTestRule.waitForIdle()
        assertFalse(testMutableUiState.isVisible.value)
    }

    @Test
    fun GIVEN_visible_state_WHEN_not_moved_outside_of_screen_THEN_visibility_is_true() = runTest {
        val childSize = 60.dp

        setupTestMutableUiState(
            childModifier = Modifier
                .requiredSize(childSize)
                .background(color = Color.Red)
        )

        val offset = childSize - 1.dp
        // moving the child to the top-right corner + offset less than its size -> make it just visible
        testMutableUiState.snapTo(
            target = TestTargetUiState(
                position = PositionAlignment.Target(
                    insideAlignment = BiasAlignment.InsideAlignment.TopEnd,
                    offset = DpOffset(x = offset, y = 0.dp)
                )
            )
        )

        // assert it's visible
        composeTestRule.waitForIdle()
        assertTrue(testMutableUiState.isVisible.value)
    }

    @Test
    fun GIVEN_visible_state_and_clipToBounds_WHEN_moved_outside_of_parent_THEN_visibility_is_false() = runTest {
        // child is in the top-left corner with the size of 60dp
        val childSize = 60.dp
        val parentSize = 120.dp
        setupTestMutableUiState(
            clipToBounds = true,
            containerModifier = Modifier
                .requiredSize(parentSize),
            childModifier = Modifier
                .requiredSize(childSize)
                .background(color = Color.Red)
        )

        // moving the child with offset that equals parent's size -> pushes it off parent's bounds
        testMutableUiState.snapTo(
            target = TestTargetUiState(
                position = PositionAlignment.Target(
                    offset = DpOffset(x = parentSize, y = 0.dp)
                )
            )
        )

        // assert it's invisible
        composeTestRule.waitForIdle()
        assertFalse(testMutableUiState.isVisible.value)
    }

    @Test
    fun GIVEN_visible_state_and_clipToBounds_WHEN_not_moved_outside_of_parent_THEN_visibility_is_true() = runTest {
        // child is in the top-left corner with the size of 60dp
        val childSize = 60.dp
        val parentSize = 120.dp
        setupTestMutableUiState(
            clipToBounds = true,
            containerModifier = Modifier
                .requiredSize(parentSize),
            childModifier = Modifier
                .requiredSize(childSize)
                .background(color = Color.Red)
        )

        // moving the child with offset that less parent's size -> just visible is parent's bounds
        val offset = parentSize - 1.dp
        testMutableUiState.snapTo(
            target = TestTargetUiState(
                position = PositionAlignment.Target(
                    offset = DpOffset(x = offset, y = 0.dp)
                )
            )
        )

        // assert it's visible
        composeTestRule.waitForIdle()
        assertTrue(testMutableUiState.isVisible.value)
    }

}
