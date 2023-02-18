package com.bumble.appyx.core.node

import androidx.lifecycle.Lifecycle
import com.bumble.appyx.core.AppyxTestScenario
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.store.RetainedInstanceStore
import com.bumble.appyx.core.store.getRetainedInstance
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class RetainedInstanceStoreTest {
    private val stubRetainedInstanceStore = StubRetainedInstanceStore()
    private var beforeNodeCreatedFunc: ((BuildContext) -> Unit)? = null

    @get:Rule
    val rule = AppyxTestScenario { buildContext ->
        beforeNodeCreatedFunc?.invoke(buildContext)
        TestParentNode(buildContext, stubRetainedInstanceStore)
    }

    @Test
    fun WHEN_activity_finished_THEN_retained_instance_store_content_is_removed() {
        rule.start()

        rule.activityScenario.moveToState(Lifecycle.State.DESTROYED)

        assertTrue(stubRetainedInstanceStore.clearStoreCalled)
    }

    @Test
    fun WHEN_activity_recreated_THEN_retained_instance_store_content_is_not_removed() {
        rule.start()

        rule.activityScenario.recreate()

        assertFalse(stubRetainedInstanceStore.clearStoreCalled)
    }

    @Test
    fun GIVEN_counter_stored_and_incremented_AND_activity_finished_WHEN_stored_counter_incremented_THEN_counter_value_is_1() {
        var nodeBuildInvocationCount = 0
        var factoryInvocationCount = 0
        var disposerCalled = false
        var buildContext: BuildContext? = null

        val getRetainedCounterFunc = {
            requireNotNull(buildContext) { "Build context not set" }
                .getRetainedInstance(
                    disposer = {
                        disposerCalled = true
                    },
                    factory = {
                        factoryInvocationCount++
                        Counter()
                    }
                )
        }
        beforeNodeCreatedFunc = {
            buildContext = it
            nodeBuildInvocationCount++
        }
        rule.start()
        getRetainedCounterFunc().increment()
        rule.activityScenario.moveToState(Lifecycle.State.DESTROYED)

        getRetainedCounterFunc().increment()

        assertEquals(1, getRetainedCounterFunc().value)
        assertEquals(1, nodeBuildInvocationCount)
        assertEquals(2, factoryInvocationCount)
        assertTrue(disposerCalled)
    }

    @Test
    fun GIVEN_counter_stored_and_incremented_AND_activity_recreated_WHEN_stored_counter_incremented_THEN_counter_value_is_2() {
        var nodeBuildInvocationCount = 0
        var factoryInvocationCount = 0
        var disposerCalled = false
        var buildContext: BuildContext? = null

        val getRetainedCounterFunc = {
            requireNotNull(buildContext) { "Build context not set" }
                .getRetainedInstance(
                    disposer = {
                        disposerCalled = true
                    },
                    factory = {
                        factoryInvocationCount++
                        Counter()
                    }
                )
        }
        beforeNodeCreatedFunc = {
            buildContext = it
            nodeBuildInvocationCount++
        }
        rule.start()
        getRetainedCounterFunc().increment()
        rule.activityScenario.recreate()

        getRetainedCounterFunc().increment()

        assertEquals(2, getRetainedCounterFunc().value)
        assertEquals(2, nodeBuildInvocationCount)
        assertEquals(1, factoryInvocationCount)
        assertFalse(disposerCalled)
    }

    class Counter {
        var value: Int = 0
            private set

        fun increment() {
            value++
        }
    }

    class TestParentNode(
        buildContext: BuildContext,
        retainedInstanceStore: RetainedInstanceStore,
    ) : Node(
        buildContext = buildContext,
        retainedInstanceStore = retainedInstanceStore,
    )

    class StubRetainedInstanceStore : RetainedInstanceStore by RetainedInstanceStore {
        var clearStoreCalled: Boolean = false

        override fun clearStore(storeId: String) {
            clearStoreCalled = true
            RetainedInstanceStore.clearStore(storeId)
        }
    }
}
