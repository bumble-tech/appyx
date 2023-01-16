package com.bumble.appyx.interactions.core

import androidx.compose.runtime.Immutable
import java.util.UUID

// FIXME @Parcelize
@Immutable
class NavKey<NavTarget> private constructor(
    val navTarget: NavTarget, // FIXME @RawValue
    val id: String
) { // FIXME : Parcelable {

    constructor(navTarget: NavTarget) : this( // FIXME @RawValue
        navTarget = navTarget,
        id = UUID.randomUUID().toString()
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NavKey<*>

        if (navTarget != other.navTarget) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = navTarget?.hashCode() ?: 0
        result = 31 * result + id.hashCode()
        return result
    }

}
