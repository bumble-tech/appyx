package com.bumble.appyx.core.lifecycle

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LifecycleExtKtTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `asFlow follows lifecycle`() {
        val lifecycle = TestLifecycle()
        runBlockingTest {
            val events = ArrayList<Lifecycle.State>()
            val task = launch { lifecycle.asFlow().collect { events += it } }

            val states = listOf(
                Lifecycle.State.CREATED,
                Lifecycle.State.STARTED,
                Lifecycle.State.RESUMED,
                Lifecycle.State.STARTED,
                Lifecycle.State.CREATED,
                Lifecycle.State.DESTROYED,
            )
            states.forEach { lifecycle.state = it }

            task.cancel()

            assertEquals(
                listOf(Lifecycle.State.INITIALIZED) + states,
                events,
            )
        }
    }

}
