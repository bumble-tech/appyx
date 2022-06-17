package com.bumble.appyx.core.integrationpoint.requestcode

import com.bumble.appyx.core.minimal.reactive.Source

interface RequestCodeBasedEventStream<T : RequestCodeBasedEventStream.RequestCodeBasedEvent> {

    fun events(client: RequestCodeClient): Source<T>

    interface RequestCodeBasedEvent {
        val requestCode: Int
    }
}
