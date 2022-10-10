package com.bumble.appyx.core.integrationpoint.requestcode

import com.bumble.appyx.core.integrationpoint.requestcode.RequestCodeBasedEventStream.RequestCodeBasedEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext

open class RequestCodeBasedEventStreamImpl<T : RequestCodeBasedEvent>(
    private val requestCodeRegistry: RequestCodeRegistry,
    protected val scope: CoroutineScope = CoroutineScope(EmptyCoroutineContext + Dispatchers.Unconfined)
) : RequestCodeBasedEventStream<T> {
    private val events = HashMap<Int, MutableSharedFlow<T>>()

    override fun events(client: RequestCodeClient): Flow<T> {
        val id = requestCodeRegistry.generateGroupId(client.requestCodeClientId)
        ensureSubject(id)

        return events.getValue(id)
    }

    private fun ensureSubject(id: Int, onSubjectDidNotExist: (() -> Unit)? = null) {
        var subjectJustCreated = false

        if (!events.containsKey(id)) {
            events[id] = MutableSharedFlow(
                replay = 0,
                extraBufferCapacity = 1,
                onBufferOverflow = BufferOverflow.DROP_OLDEST,
            )
            subjectJustCreated = true
        }

        if (subjectJustCreated) {
            onSubjectDidNotExist?.invoke()
        }
    }

    private fun flushResultWhenSubscribersAppear(flow: MutableSharedFlow<T>, event: T) {
        scope.launch {
            flow.subscriptionCount.collectLatest { subscriptionCount ->
                if (subscriptionCount > 0) {
                    flow.emit(event)
                    this@launch.cancel()
                }
            }
        }
    }

    protected fun publish(externalRequestCode: Int, event: T) {
        val id = requestCodeRegistry.resolveGroupId(externalRequestCode)

        ensureSubject(id)

        val flow = events.getValue(id)
        // It's possible that publishing can happen before we have any subscriber. For instance,
        // with don't keep activities onActivityResult can be called before the Node is ready. Here we're caching
        // the result and flushing it when clients have subscribed.
        // Flushing results in flow.onSubscription won't suffice as it will be called after the first
        // subscription and the other subscribers will not receive the cached event
        if (flow.subscriptionCount.value == 0) {
            flushResultWhenSubscribersAppear(flow, event)
        } else {
            flow.tryEmit(event)
        }
    }

    protected fun Int.toInternalRequestCode() =
        requestCodeRegistry.resolveRequestCode(this)

    protected fun RequestCodeClient.forgeExternalRequestCode(internalRequestCode: Int) =
        requestCodeRegistry.generateRequestCode(this.requestCodeClientId, internalRequestCode)
}
