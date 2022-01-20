package com.bumble.appyx.v2.core.integrationpoint.requestcode

import com.bumble.appyx.v2.core.minimal.reactive.Source

interface RequestCodeBasedEventStream<T : RequestCodeBasedEventStream.RequestCodeBasedEvent> {

    fun events(client: RequestCodeClient): Source<T>

    interface RequestCodeBasedEvent {
        val requestCode: Int
    }
}
