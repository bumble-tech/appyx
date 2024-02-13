package com.bumble.appyx.interactions.model


import androidx.compose.runtime.Immutable
import com.bumble.appyx.interactions.UUID
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize
import com.bumble.appyx.utils.multiplatform.RawValue

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
