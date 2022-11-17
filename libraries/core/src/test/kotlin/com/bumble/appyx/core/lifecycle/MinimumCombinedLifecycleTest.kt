package com.bumble.appyx.core.lifecycle

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle.State
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MinimumCombinedLifecycleTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `completely duplicates state of single lifecycle`() {
        val test = TestLifecycle()
        val combined = MinimumCombinedLifecycle(test.lifecycle)
        STATES.forEach {
            if (it != State.INITIALIZED) {
                test.state = it
            }
            assertEquals(it, combined.lifecycle.currentState)
        }
    }

    @Test
    fun `completely duplicates state of lifecycles with same state`() {
        val test1 = TestLifecycle()
        val test2 = TestLifecycle()
        val combined = MinimumCombinedLifecycle(test1.lifecycle, test2.lifecycle)
        STATES.forEach {
            if (it != State.INITIALIZED) {
                test1.state = it
                test2.state = it
            }
            assertEquals(it, combined.lifecycle.currentState)
        }
    }

    @Test
    fun `destroyed when one of lifecycles is destroyed`() {
        val test1 = TestLifecycle(State.RESUMED)
        val test2 = TestLifecycle(State.RESUMED)
        val combined = MinimumCombinedLifecycle(test1.lifecycle, test2.lifecycle)
        test1.state = State.DESTROYED
        assertEquals(State.DESTROYED, combined.lifecycle.currentState)
    }

    companion object {
        val STATES =
            listOf(State.INITIALIZED, State.CREATED, State.STARTED, State.RESUMED, State.DESTROYED)
    }

}
