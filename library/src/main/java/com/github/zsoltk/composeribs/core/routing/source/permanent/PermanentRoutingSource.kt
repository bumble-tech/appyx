package com.github.zsoltk.composeribs.core.routing.source.permanent

import android.os.Parcelable
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

class PermanentRoutingSource<Key>(
    configuration: List<Key>,
) : RoutingSource<Key, Int> {

    @Parcelize
    data class RoutingKeyImpl<Key>(
        override val routing: @RawValue Key,
        private val id: Int,
    ) : RoutingKey<Key>, Parcelable

    constructor(vararg configuration: Key) : this(configuration.toList())

    private var idCounter: Int = 0

    private val state = MutableStateFlow(
        configuration.map { key ->
            PermanentElement(
                key = RoutingKeyImpl(routing = key, id = idCounter++),
                fromState = 0,
                targetState = 0,
            )
        }
    )

    override val all: StateFlow<PermanentElements<Key>>
        get() = state

    override val onScreen: StateFlow<PermanentElements<Key>>
        get() = state

    override val offScreen: StateFlow<PermanentElements<Key>> =
        MutableStateFlow(emptyList())

    override val canHandleBackPress: StateFlow<Boolean> =
        MutableStateFlow(false)

    override fun onBackPressed() {
        // no-op
    }

    override fun onTransitionFinished(key: RoutingKey<Key>) {
        // no-op
    }

}
