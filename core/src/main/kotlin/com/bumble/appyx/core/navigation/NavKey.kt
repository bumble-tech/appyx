package com.bumble.appyx.core.navigation

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.util.UUID

@Parcelize
@Immutable
class NavKey<NavTarget> private constructor(
    val navTarget: @RawValue NavTarget,
    val id: String
) : Parcelable {

    constructor(navTarget: @RawValue NavTarget) : this(
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
