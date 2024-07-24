package com.bumble.appyx.utils.interop.rx3.connectable

import android.annotation.SuppressLint
import com.bumble.appyx.navigation.lifecycle.Lifecycle
import com.jakewharton.rxrelay3.PublishRelay
import com.jakewharton.rxrelay3.Relay
import io.reactivex.rxjava3.core.Observer

class NodeConnector<Input : Any, Output : Any> : Connectable<Input, Output> {

    override fun onSetupTooling(lifecycle: Lifecycle) {
        flushInputCache()
        flushOutputCache()
    }

    private val intakeInput: Relay<Input> = PublishRelay.create()
    private val exhaustInput: Relay<Input> = PublishRelay.create()
    private var isInputFlushed = false
    private val inputCache = mutableListOf<Input>()

    override val input: Relay<Input> = object : Relay<Input>() {

        override fun subscribeActual(observer: Observer<in Input>) {
            exhaustInput.subscribe(observer)
        }

        override fun accept(value: Input) {
            intakeInput.accept(value)
        }

        override fun hasObservers() = exhaustInput.hasObservers()

    }

    private val inputCacheSubscription = intakeInput.subscribe {
        synchronized(this) {
            if (!isInputFlushed) {
                inputCache.add(it)
            } else {
                exhaustInput.accept(it)
                switchToInputExhaust()
            }
        }
    }

    private fun flushInputCache() {
        synchronized(this) {
            if (isInputFlushed) error("Input already flushed")
            isInputFlushed = true
            inputCache.forEach { exhaustInput.accept(it) }
            inputCache.clear()
        }
    }

    @SuppressLint("CheckResult")
    private fun switchToInputExhaust() {
        intakeInput.subscribe { exhaustInput.accept(it) }
        inputCacheSubscription.dispose()
    }


    private val intakeOutput: Relay<Output> = PublishRelay.create()
    private val exhaustOutput: Relay<Output> = PublishRelay.create()
    private var isOutputFlushed = false
    private val outputCache = mutableListOf<Output>()

    override val output: Relay<Output> = object : Relay<Output>() {

        override fun subscribeActual(observer: Observer<in Output>) {
            exhaustOutput.subscribe(observer)
        }

        override fun accept(value: Output) {
            intakeOutput.accept(value)
        }

        override fun hasObservers() = exhaustOutput.hasObservers()

    }

    private val outputCacheSubscription = intakeOutput.subscribe {
        synchronized(this) {
            if (!isOutputFlushed) {
                outputCache.add(it)
            } else {
                exhaustOutput.accept(it)
                switchToOutputExhaust()
            }
        }
    }

    private fun flushOutputCache() {
        synchronized(this) {
            if (isOutputFlushed) error("Output already flushed")
            isOutputFlushed = true
            outputCache.forEach { exhaustOutput.accept(it) }
            outputCache.clear()
        }
    }

    @SuppressLint("CheckResult")
    private fun switchToOutputExhaust() {
        intakeOutput.subscribe { exhaustOutput.accept(it) }
        outputCacheSubscription.dispose()
    }
}
