package com.bumble.appyx.core.integrationpoint.requestcode

import com.bumble.appyx.Appyx
import com.bumble.appyx.core.integrationpoint.requestcode.RequestCodeBasedEventStream.RequestCodeBasedEvent
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

abstract class RequestCodeBasedEventStreamImpl<T : RequestCodeBasedEvent>(
    private val requestCodeRegistry: RequestCodeRegistry
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

    protected fun publish(externalRequestCode: Int, event: T) {
        val id = requestCodeRegistry.resolveGroupId(externalRequestCode)

        ensureSubject(id) {
            val internalRequestCode = externalRequestCode.toInternalRequestCode()
        }

        events.getValue(id).tryEmit(event)
    }

    protected fun Int.toInternalRequestCode() =
        requestCodeRegistry.resolveRequestCode(this)

    protected fun RequestCodeClient.forgeExternalRequestCode(internalRequestCode: Int) =
        requestCodeRegistry.generateRequestCode(this.requestCodeClientId, internalRequestCode)
}
