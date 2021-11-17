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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RoutingKey<*>

        if (routing != other.routing) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = routing?.hashCode() ?: 0
        result = 31 * result + id.hashCode()
        return result
    }

}
