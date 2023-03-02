package com.bumble.appyx.interactions.core


import androidx.compose.runtime.Immutable
import com.bumble.appyx.interactions.Parcelable
import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.RawValue
import com.bumble.appyx.interactions.core.ui.NavElements
import java.util.UUID

@Parcelize
@Immutable
data class NavElement<InteractionTarget>(
    val interactionTarget: @RawValue InteractionTarget,
    val id: String = UUID.randomUUID().toString()
) : Parcelable

fun <InteractionTarget> InteractionTarget.asElement(): NavElement<InteractionTarget> =
    NavElement(this)

fun <InteractionTarget> List<InteractionTarget>.asElements(): NavElements<InteractionTarget> =
    map { it.asElement() }
