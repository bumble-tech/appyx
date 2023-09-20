package com.bumble.appyx.navigation.store

import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

@Suppress("TestFunctionName")
class RetainedInstanceStoreTest {

    private val storeId = "storeId"
    private val key = "key"
    private val store = RetainedInstanceStoreImpl()

    @Test
    fun GIVEN_object_stored_WHEN_get_with_same_identifier_THEN_same_instance_is_retrieved() {
        val obj = Any()
        store.get(storeId, key) { obj }

        val retrieved = store.get(storeId, key) { Any() }

        assertSame(obj, retrieved)
    }

    @Test
    fun GIVEN_object_stored_WHEN_get_with_same_identifier_THEN_factory_not_called() {
        var factoryCalled = false
        store.get(storeId, key) { Any() }

        store.get(storeId, key) {
            factoryCalled = true
            Any()
        }

        assertFalse(factoryCalled)
    }

    // This test requires reflection so can only be executed in JVM builds.
    @Suppress("ForbiddenComment")
    // TODO: move to desktop or android only tests, not common test
    @Ignore
    @Test
    fun GIVEN_two_objects_with_different_types_stored_WHEN_get_with_same_identifier_THEN_both_objects_returned() {
        store.get(storeId, key) { 1 }
        store.get(storeId, key) { 2L }

        val integerValue = store.get(storeId, key) { 5 }
        val longValue = store.get(storeId, key) { 6L }

        assertEquals(1, integerValue)
        assertEquals(2L, longValue)
    }

    @Test
    fun GIVEN_two_objects_stored_with_same_type_AND_different_keys_WHEN_get_with_same_identifier_THEN_both_objects_returned() {
        store.get(storeId = storeId, key = "1") { 1 }
        store.get(storeId = storeId, key = "2") { 2 }

        val integerValue1 = store.get(storeId = storeId, key = "1") { 5 }
        val integerValue2 = store.get(storeId = storeId, key = "2") { 6 }

        assertEquals(1, integerValue1)
        assertEquals(2, integerValue2)
    }

    @Test
    fun GIVEN_object_stored_WHEN_clearStore_with_same_identifier_THEN_object_is_disposed() {
        val obj = Any()
        var disposed = false
        store.get(storeId, key = key, disposer = { disposed = true }) { obj }

        store.clearStore(storeId)

        assertTrue(disposed)
    }

    @Test
    fun GIVEN_object_stored_WHEN_clearStore_with_different_identifier_THEN_object_is_not_disposed() {
        val obj = Any()
        val otherIdentifier = "other"
        var disposed = false
        store.get(storeId, key) { obj }
        store.get(otherIdentifier, key, disposer = { disposed = true }) { obj }

        store.clearStore(storeId)

        assertFalse(disposed)
    }

    @Test
    fun GIVEN_object_stored_before_WHEN_checking_if_same_instance_retained_by_store_id_with_same_owner_THEN_expect_true() {
        val obj = Any()
        store.get(storeId, key) { obj }

        val isRetained = store.isRetainedByStoreId(storeId, obj)

        assertTrue(isRetained)
    }

    @Test
    fun GIVEN_object_stored_before_WHEN_checking_if_same_instance_retained_by_store_id_with_different_owner_THEN_expect_false() {
        val obj = Any()
        store.get(storeId, key) { obj }

        val isRetained = store.isRetainedByStoreId("different-identifier", obj)

        assertFalse(isRetained)
    }

    @Test
    fun GIVEN_object_stored_before_WHEN_checking_if_different_instance_retained_by_store_id_with_same_owner_THEN_expect_false() {
        val obj = Any()
        val otherObj = Any()
        store.get(storeId, key) { obj }

        val isRetained = store.isRetainedByStoreId(storeId, otherObj)

        assertFalse(isRetained)
    }

    @Test
    fun GIVEN_object_stored_before_AND_then_removed_WHEN_checking_if_same_instance_retained_by_store_id_with_same_owner_THEN_expect_false() {
        val obj = Any()
        store.get(storeId, key) { obj }
        store.clearStore(storeId)

        val isRetained = store.isRetainedByStoreId(storeId, obj)

        assertFalse(isRetained)
    }

    @Test
    fun GIVEN_no_object_stored_WHEN_checking_if_instance_retained_by_store_id_THEN_expect_false() {
        val obj = Any()

        val isRetained = store.isRetainedByStoreId(storeId, obj)

        assertFalse(isRetained)
    }

    @Test
    fun GIVEN_object_stored_before_WHEN_checking_if_same_instance_retained_THEN_expect_true() {
        val obj = Any()
        store.get(storeId, key) { obj }

        val isRetained = store.isRetained(obj)

        assertTrue(isRetained)
    }

    @Test
    fun GIVEN_object_stored_before_WHEN_checking_if_different_instance_retained_THEN_expect_false() {
        val obj = Any()
        val otherObj = Any()
        store.get(storeId, key) { obj }

        val isRetained = store.isRetained(otherObj)

        assertFalse(isRetained)
    }

    @Test
    fun GIVEN_object_stored_before_AND_then_removed_WHEN_checking_if_same_instance_retained_THEN_expect_false() {
        val obj = Any()
        store.get(storeId, key) { obj }
        store.clearStore(storeId)

        val isRetained = store.isRetained(obj)

        assertFalse(isRetained)
    }

    @Test
    fun GIVEN_no_object_stored_WHEN_checking_if_instance_retained_THEN_expect_false() {
        val obj = Any()

        val isRetained = store.isRetained(obj)

        assertFalse(isRetained)
    }

}
