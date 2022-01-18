package com.github.zsoltk.composeribs.core.integrationpoint.requestcode

import com.github.zsoltk.composeribs.core.minimal.reactive.Source

interface RequestCodeBasedEventStream<T : RequestCodeBasedEventStream.RequestCodeBasedEvent> {

    fun events(client: RequestCodeClient): Source<T>

    interface RequestCodeBasedEvent {
        val requestCode: Int
    }
}
