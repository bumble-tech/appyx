package com.github.zsoltk.composeribs.minimal.state

import com.github.zsoltk.composeribs.minimal.reactive.Cancellable
import com.github.zsoltk.composeribs.minimal.reactive.Relay
import com.github.zsoltk.composeribs.minimal.reactive.Source

abstract class Store<State>(
    initialState: State
) : Source<State>, Cancellable {
    private val initThread = Thread.currentThread()

    private var isCancelled = false
    var state: State = initialState
        private set

    private val relay: Relay<State> = Relay()

    protected fun emit(value: State) {
        if (isCancelled) return

        verifyThread()

        this.state = value
        relay.emit(value)
    }

    private fun verifyThread() {
        if (initThread != Thread.currentThread()) {
            throw SameThreadVerificationException(
                "Store functions should be called on the same thread where store is initialized. " +
                    "Current: ${Thread.currentThread().name}, initial: ${initThread.name}."
            )
        }
    }

    override fun cancel() {
        isCancelled = true
    }

    override fun observe(callback: (State) -> Unit): Cancellable {
        verifyThread()

        callback(state)
        return relay.observe(callback)
    }
}

abstract class AsyncStore<Event, State>(initialState: State) : Store<State>(initialState) {
    private val eventRelay = Relay<Event>()
    private val cancellable = eventRelay.observe {
        emit(reduceEvent(it, state))
    }

    protected abstract fun reduceEvent(event: Event, state: State): State

    protected fun emitEvent(event: Event) {
        eventRelay.emit(event)
    }

    override fun cancel() {
        super.cancel()
        cancellable.cancel()
    }
}

class SameThreadVerificationException(message: String) : IllegalStateException(message)
