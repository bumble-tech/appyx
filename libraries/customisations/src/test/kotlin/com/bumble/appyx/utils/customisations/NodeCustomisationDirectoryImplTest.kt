package com.bumble.appyx.utils.customisations

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class NodeCustomisationDirectoryImplTest {
    private val directory = NodeCustomisationDirectoryImpl()

    @Test
    fun `GIVEN put lazy WHEN get not invoked THEN customisation provider not invoked`() {
        var providerInvoked = false
        directory.putLazy(TestCustomisation::class) {
            providerInvoked = true
            TestCustomisation("lazy")
        }

        assertFalse(providerInvoked)
    }

    @Test
    fun `GIVEN put lazy WHEN get invoked THEN customisation provider invoked`() {
        directory.putLazy(TestCustomisation::class) {
            TestCustomisation("lazy")
        }
        assertEquals(TestCustomisation("lazy"), directory.get(TestCustomisation::class))
    }

    @Test
    fun `GIVEN put lazy subdirectory WHEN subdirectory not invoked THEN subdirectory block not invoked`() {
        var customisationDirectoryBlockInvoked = false
        directory.putSubDirectoryLazy(TestRib::class) {
            customisationDirectoryBlockInvoked = true
            NodeCustomisationDirectoryImpl(directory)
        }

        assertFalse(customisationDirectoryBlockInvoked)
    }

    @Test
    fun `GIVEN put lazy subdirectory WHEN subdirectory invoked THEN subdirectory block invoked`() {
        var customisationDirectoryBlockInvoked = false
        directory.putSubDirectoryLazy(TestRib::class) {
            customisationDirectoryBlockInvoked = true
            NodeCustomisationDirectoryImpl(directory)
        }

        directory.getSubDirectory(TestRib::class)

        assertTrue(customisationDirectoryBlockInvoked)
    }

    @Test
    fun `GIVEN subdirectory created via kclass extension WHEN subdirectory not invoked THEN subdirectory block not invoked`() {
        var customisationDirectoryBlockInvoked = false
        directory.apply {
            TestRib::class {
                customisationDirectoryBlockInvoked = true
            }
        }

        assertFalse(customisationDirectoryBlockInvoked)
    }

    @Test
    fun `GIVEN subdirectory created via kclass extension WHEN subdirectory invoked THEN subdirectory block invoked`() {
        var customisationDirectoryBlockInvoked = false
        directory.apply {
            TestRib::class {
                customisationDirectoryBlockInvoked = true
            }
        }

        directory.getSubDirectory(TestRib::class)

        assertTrue(customisationDirectoryBlockInvoked)
    }

    private data class TestCustomisation(val label: String) : NodeCustomisation

    interface TestRib
}
