package com.bumble.appyx.utils.interop.rx3.plugin

import com.bumble.appyx.navigation.plugin.Destroyable
import io.reactivex.rxjava3.disposables.Disposable
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class DisposeOnDestroyTest {
    @Test
    fun `WHEN dispose on destroy plugin created THEN verify is destroyable type`() {
        assertInstanceOf(Destroyable::class.java, disposeOnDestroyPlugin())
    }

    @Test
    fun `GIVEN dispose on destroy plugin created with disposable WHEN destroy THEN disposable is disposed`() {
        val disposable = Disposable.empty()
        val disposeOnDestroyPlugin = disposeOnDestroyPlugin(disposable)

        (disposeOnDestroyPlugin as Destroyable).destroy()

        assertTrue(disposable.isDisposed)
    }

    @Test
    fun `GIVEN dispose on destroy plugin created with multiple disposables WHEN destroy THEN all disposables are disposed`() {
        val disposable1 = Disposable.empty()
        val disposable2 = Disposable.empty()
        val disposeOnDestroyPlugin = disposeOnDestroyPlugin(disposable1, disposable2)

        (disposeOnDestroyPlugin as Destroyable).destroy()

        assertTrue(disposable1.isDisposed)
        assertTrue(disposable2.isDisposed)
    }

    @Test
    fun `WHEN dispose on destroy plugin created with disposable THEN disposable is not disposed`() {
        val disposable = Disposable.empty()
        disposeOnDestroyPlugin(disposable)

        assertFalse(disposable.isDisposed)
    }
}
