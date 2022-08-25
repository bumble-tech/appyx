package com.bumble.appyx.interop.rx2.adapter

import com.bumble.appyx.core.minimal.reactive.Cancellable
import com.bumble.appyx.core.minimal.reactive.Source
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class Rx2ExtensionsTest {

    @Test
    fun `WHEN source emits an event THEN the event is emitted to the Observable`() {
        val source = StubSource<Boolean>()
        val testObserver = source.rx2().test()

        source.emit(true)

        testObserver.assertValue { true }
    }

    @Test
    fun `WHEN Observable is canceled THEN Source is not subscribed`() {
        val source = StubSource<Boolean>()
        val testObserver = source.rx2().test()

        testObserver.cancel()

        assertTrue(source.listeners.isEmpty())
    }

    private class StubSource<T> : Source<T> {

        var listeners: List<(T) -> Unit> = emptyList()
            private set

        override fun observe(callback: (T) -> Unit): Cancellable {
            listeners = listeners + callback
            return Cancellable.cancellableOf {
                listeners = listeners - callback
            }
        }

        fun emit(value: T) {
            listeners.forEach { it.invoke(value) }
        }
    }
}
