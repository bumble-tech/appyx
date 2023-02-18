package com.bumble.appyx.core.store

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class RetainedInstanceStoreTest {

    private val identifier = "identifier"
    private val store = RetainedInstanceStoreImpl()

    @Test
    fun `GIVEN object stored WHEN get with same identifier THEN same instance is retrieved`() {
        val obj = Any()
        store.get(identifier) { obj }

        val retrieved = store.get(identifier) { Any() }

        assertSame(obj, retrieved)
    }

    @Test
    fun `GIVEN object stored WHEN get with same identifier THEN factory not called`() {
        var factoryCalled = false
        store.get(identifier) { Any() }

        store.get(identifier) {
            factoryCalled = true
            Any()
        }

        assertFalse(factoryCalled)
    }

    @Test
    fun `GIVEN two objects with different types stored WHEN get with same identifier THEN both objects returned`() {
        store.get(identifier) { 1 }
        store.get(identifier) { 2L }

        val integerValue = store.get(identifier) { 5 }
        val longValue = store.get(identifier) { 6L }

        assertEquals(1, integerValue)
        assertEquals(2L, longValue)
    }

    @Test
    fun `GIVEN two objects stored with same type AND different keys WHEN get with same identifier THEN both objects returned`() {
        store.get(storeId = identifier, key = "1") { 1 }
        store.get(storeId = identifier, key = "2") { 2 }

        val integerValue1 = store.get(storeId = identifier, key = "1") { 5 }
        val integerValue2 = store.get(storeId = identifier, key = "2") { 6L }

        assertEquals(1, integerValue1)
        assertEquals(2, integerValue2)
    }

    @Test
    fun `GIVEN object stored WHEN clearStore with same identifier THEN object is disposed`() {
        val obj = Any()
        var disposed = false
        store.get(identifier, disposer = { disposed = true }) { obj }

        store.clearStore(identifier)

        assertTrue(disposed)
    }

    @Test
    fun `GIVEN object stored WHEN clearStore with different identifier THEN object is not disposed`() {
        val obj = Any()
        val otherIdentifier = "other"
        var disposed = false
        store.get(identifier) { obj }
        store.get(otherIdentifier, disposer = { disposed = true }) { obj }

        store.clearStore(identifier)

        assertFalse(disposed)
    }
}
