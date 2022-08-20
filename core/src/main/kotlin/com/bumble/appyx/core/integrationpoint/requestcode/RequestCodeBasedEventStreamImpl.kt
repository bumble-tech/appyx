package com.bumble.appyx.core.integrationpoint.requestcode

import android.util.Log
import com.bumble.appyx.core.integrationpoint.requestcode.RequestCodeBasedEventStream.RequestCodeBasedEvent
import com.bumble.appyx.core.minimal.reactive.Relay
import com.bumble.appyx.core.minimal.reactive.Source

abstract class RequestCodeBasedEventStreamImpl<T : RequestCodeBasedEvent>(
    private val requestCodeRegistry: RequestCodeRegistry
) : RequestCodeBasedEventStream<T> {
    private val events = HashMap<Int, Relay<T>>()

    override fun events(client: RequestCodeClient): Source<T> {
        val id = requestCodeRegistry.generateGroupId(client.requestCodeClientId)
        ensureSubject(id)

        return events.getValue(id)
    }

    private fun ensureSubject(id: Int, onSubjectDidNotExist: (() -> Unit)? = null) {
        var subjectJustCreated = false

        if (!events.containsKey(id)) {
            events[id] = Relay()
            subjectJustCreated = true
        }

        if (subjectJustCreated) {
            onSubjectDidNotExist?.invoke()
        }
    }

    protected fun publish(externalRequestCode: Int, event: T) {
        val id = requestCodeRegistry.resolveGroupId(externalRequestCode)
        val internalRequestCode = externalRequestCode.toInternalRequestCode()

        ensureSubject(id) {
            Log.e( "RIBs", "There's no one listening for request code event! " +
                    "requestCode: $externalRequestCode, " +
                    "resolved group: $id, " +
                    "resolved code: $internalRequestCode, " +
                    "event: $event")
        }

        events.getValue(id).emit(event)
    }

    protected fun Int.toInternalRequestCode() =
        requestCodeRegistry.resolveRequestCode(this)

    protected fun RequestCodeClient.forgeExternalRequestCode(internalRequestCode: Int) =
        requestCodeRegistry.generateRequestCode(this.requestCodeClientId, internalRequestCode)
}
