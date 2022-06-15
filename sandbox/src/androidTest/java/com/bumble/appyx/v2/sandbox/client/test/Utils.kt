package com.bumble.appyx.v2.sandbox.client.test

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.observers.TestObserver
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.greaterThan

fun <T> ObservableSource<out T>.wrapToObservable(): Observable<T> = Observable.wrap(cast())

inline fun <reified T> Any?.cast(): T = this as T

fun <T> TestObserver<T>.assertLastValueEqual(value: T) {
    assertThat(this.valueCount(), greaterThan(0))
    this.assertValueAt(valueCount() - 1, value)
}
