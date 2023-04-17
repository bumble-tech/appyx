package com.bumble.appyx.interactions.core


import androidx.compose.runtime.Immutable
import com.bumble.appyx.interactions.Parcelable
import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.RawValue
import com.bumble.appyx.interactions.UUID

@Parcelize
@Immutable
data class Element<InteractionTarget>(
    val interactionTarget: @RawValue InteractionTarget,
    val id: String = UUID.randomUUID()
) : Parcelable

fun <InteractionTarget> InteractionTarget.asElement(): Element<InteractionTarget> =
    Element(this)

fun <InteractionTarget> List<InteractionTarget>.asElements(): Elements<InteractionTarget> =
    map { it.asElement() }
