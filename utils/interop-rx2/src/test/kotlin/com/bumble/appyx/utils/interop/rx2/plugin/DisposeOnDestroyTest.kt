package com.bumble.appyx.utils.interop.rx2.plugin

import com.bumble.appyx.navigation.plugin.Destroyable
import com.bumble.appyx.utils.interop.rx2.plugin.disposeOnDestroyPlugin
import io.reactivex.disposables.Disposables
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
        val disposable = Disposables.empty()
        val disposeOnDestroyPlugin = disposeOnDestroyPlugin(disposable)

        (disposeOnDestroyPlugin as Destroyable).destroy()

        assertTrue(disposable.isDisposed)
    }

    @Test
    fun `GIVEN dispose on destroy plugin created with multiple disposables WHEN destroy THEN all disposables are disposed`() {
        val disposable1 = Disposables.empty()
        val disposable2 = Disposables.empty()
        val disposeOnDestroyPlugin = disposeOnDestroyPlugin(disposable1, disposable2)

        (disposeOnDestroyPlugin as Destroyable).destroy()

        assertTrue(disposable1.isDisposed)
        assertTrue(disposable2.isDisposed)
    }

    @Test
    fun `WHEN dispose on destroy plugin created with disposable THEN disposable is not disposed`() {
        val disposable = Disposables.empty()
        disposeOnDestroyPlugin(disposable)

        assertFalse(disposable.isDisposed)
    }
}
