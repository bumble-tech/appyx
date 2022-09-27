package com.bumble.appyx.core.integrationpoint.requestcode

import android.os.Handler
import android.os.Looper
import com.bumble.appyx.Appyx
import com.bumble.appyx.core.integrationpoint.requestcode.RequestCodeBasedEventStream.RequestCodeBasedEvent
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

abstract class RequestCodeBasedEventStreamImpl<T : RequestCodeBasedEvent>(
    private val requestCodeRegistry: RequestCodeRegistry
) : RequestCodeBasedEventStream<T> {
    private val events = HashMap<Int, MutableSharedFlow<T>>()

    private var isSetupFinished = false
    private val pendingOperations: MutableList<Runnable> = mutableListOf()
    private val handler = Handler(Looper.getMainLooper())

    fun finishSetup() {
        if (!isSetupFinished) {
            pendingOperations.forEach {
                handler.post(it)
            }
            pendingOperations.clear()
            isSetupFinished = true
        }
    }

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


    private fun publish(externalRequestCode: Int, event: T) {
        val id = requestCodeRegistry.resolveGroupId(externalRequestCode)

        ensureSubject(id) {
            val internalRequestCode = externalRequestCode.toInternalRequestCode()
            Appyx.reportException(
                IllegalStateException(
                    "There's no one listening for request code event! " +
                            "requestCode: $externalRequestCode, " +
                            "resolved group: $id, " +
                            "resolved code: $internalRequestCode, " +
                            "event: $event"
                )
            )
        }

        events.getValue(id).tryEmit(event)
    }

    protected fun publishSafely(externalRequestCode: Int, event: T) {
        if (isSetupFinished) {
            publish(externalRequestCode, event)
        } else {
            pendingOperations.add { publish(externalRequestCode, event) }
        }
    }

    protected fun Int.toInternalRequestCode() =
        requestCodeRegistry.resolveRequestCode(this)

    protected fun RequestCodeClient.forgeExternalRequestCode(internalRequestCode: Int) =
        requestCodeRegistry.generateRequestCode(this.requestCodeClientId, internalRequestCode)
}
