package com.bumble.appyx.v2.sandbox.client.test

import androidx.test.platform.app.InstrumentationRegistry
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.observers.TestObserver
import java.util.concurrent.atomic.AtomicReference
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.greaterThan

fun <T> runOnMainSync(block: () -> T): T {
    val result = AtomicReference<T>()
    InstrumentationRegistry.getInstrumentation().runOnMainSync {
        result.set(block())
    }

    return result.get()
}

fun <T> ObservableSource<out T>.wrapToObservable(): Observable<T> = Observable.wrap(cast())

inline fun <reified T> Any?.cast(): T = this as T

fun <T> TestObserver<T>.assertLastValueEqual(value: T) {
    assertThat(this.valueCount(), greaterThan(0))
    this.assertValueAt(valueCount() - 1, value)
}
