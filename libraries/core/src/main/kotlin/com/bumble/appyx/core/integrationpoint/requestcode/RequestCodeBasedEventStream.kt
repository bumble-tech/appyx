package com.bumble.appyx.core.integrationpoint.requestcode

import kotlinx.coroutines.flow.Flow

@Deprecated("Use AndroidX API")
interface RequestCodeBasedEventStream<T : RequestCodeBasedEventStream.RequestCodeBasedEvent> {

    fun events(client: RequestCodeClient): Flow<T>

    interface RequestCodeBasedEvent {
        val requestCode: Int
    }
}
