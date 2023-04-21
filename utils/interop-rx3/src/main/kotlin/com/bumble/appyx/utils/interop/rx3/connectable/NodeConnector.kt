package com.bumble.appyx.utils.interop.rx3.connectable

import android.annotation.SuppressLint
import androidx.lifecycle.Lifecycle
import com.bumble.appyx.navigation.lifecycle.subscribe
import com.jakewharton.rxrelay3.PublishRelay
import com.jakewharton.rxrelay3.Relay
import io.reactivex.rxjava3.core.Observer

class NodeConnector<Input, Output : Any>(
    override val input: Relay<Input> = PublishRelay.create(),
) : Connectable<Input, Output> {

    private val intake: Relay<Output> = PublishRelay.create()
    private val exhaust: Relay<Output> = PublishRelay.create()
    private var isFlushed = false
    private val outputCache = mutableListOf<Output>()

    override val output: Relay<Output> = object : Relay<Output>() {

        override fun subscribeActual(observer: Observer<in Output>) {
            exhaust.subscribe(observer)
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

    @SuppressLint("CheckResult")
    private fun switchToExhaust() {
        intake.subscribe { exhaust.accept(it) }
        cacheSubscription.dispose()
    }
}
