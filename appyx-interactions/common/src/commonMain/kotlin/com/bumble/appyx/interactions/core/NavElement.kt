package com.bumble.appyx.interactions.core


import androidx.compose.runtime.Immutable
import com.bumble.appyx.interactions.Parcelable
import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.RawValue
import com.bumble.appyx.interactions.core.ui.NavElements
import java.util.UUID

@Parcelize
@Immutable
data class NavElement<NavTarget>(
    val navTarget: @RawValue NavTarget,
    val id: String = UUID.randomUUID().toString()
) : Parcelable

fun <NavTarget> NavTarget.asElement(): NavElement<NavTarget> =
    NavElement(this)

fun <NavTarget> List<NavTarget>.asElements(): NavElements<NavTarget> =
    map { it.asElement() }
