package com.bumble.appyx.v2.connectable.rx2

import androidx.lifecycle.Lifecycle
import com.bumble.appyx.v2.core.lifecycle.subscribe
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observer

class NodeConnector<Input, Output>(
    override val input: Relay<Input> = PublishRelay.create(),
) : Connectable<Input, Output> {


    private val intake: Relay<Output> = PublishRelay.create()
    private val exhaust: Relay<Output> = PublishRelay.create()
    private var isFlushed = false
    private val outputCache = mutableListOf<Output>()

    override val output: Relay<Output> = object : Relay<Output>() {

        override fun subscribeActual(observer: Observer<in Output>?) {
            exhaust.subscribe(observer as Observer<Output>)
        }

        override fun accept(value: Output) {
            intake.accept(value)
        }

        override fun hasObservers() = exhaust.hasObservers()

    }

    override fun onCreate(lifecycle: Lifecycle) {
        lifecycle.subscribe(onCreate = { flushOutputCache() })
    }

    private val cacheSubscription = intake.subscribe {
        synchronized(this) {
            if (!isFlushed) {
                outputCache.add(it)
            } else {
                exhaust.accept(it)
                switchToExhaust()
            }
        }
    }

    private fun flushOutputCache() {
        synchronized(this) {
            if (isFlushed) error("Already flushed")
            isFlushed = true
            outputCache.forEach { exhaust.accept(it) }
            outputCache.clear()
        }
    }

    private fun switchToExhaust() {
        intake.subscribe { exhaust.accept(it) }
        cacheSubscription.dispose()
    }
}
