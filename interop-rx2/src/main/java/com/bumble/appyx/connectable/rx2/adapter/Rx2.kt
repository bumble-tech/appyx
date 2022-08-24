package com.bumble.appyx.connectable.rx2.adapter

import com.bumble.appyx.core.minimal.reactive.Source
import io.reactivex.Observable

fun <T> Source<T>.rx2(): Observable<T> =
    Observable.create { emitter ->
        val cancellable = observe { emitter.onNext(it) }
        emitter.setCancellable { cancellable.cancel() }
    }
