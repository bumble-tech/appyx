package com.bumble.appyx.interop.rx2.adapter

import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.rx2.asObservable

// Previously converted custom Source<T> into RxJava2
@Deprecated(
    message = "Use asObservable() from kotlinx-coroutines-rx2",
    replaceWith = ReplaceWith("asObservable()", "kotlinx.coroutines.rx2.asObservable"),
)
fun <T : Any> Flow<T>.rx2(): Observable<T> = asObservable()
