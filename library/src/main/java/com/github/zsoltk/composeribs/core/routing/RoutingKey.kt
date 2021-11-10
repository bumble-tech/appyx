package com.github.zsoltk.composeribs.core.routing

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.util.UUID

@Parcelize
class RoutingKey<Key> private constructor(
    val routing: @RawValue Key,
    val id: String
) : Parcelable {

    constructor(routing: @RawValue Key) : this(
        routing = routing,
        id = UUID.randomUUID().toString()
    )
}
