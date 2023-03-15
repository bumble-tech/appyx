package com.bumble.appyx.core.navigation

import com.bumble.appyx.core.withPrevious
import kotlinx.coroutines.flow.map

internal fun <NavTarget, State> NavModel<NavTarget, State>.removedElementKeys() =
    this
        .elements
        .withPrevious()
        .map { values ->
            val previousKeys = values.previous?.map { it.key }.orEmpty()
            val currentKeys = values.current.map { it.key }
            previousKeys.filter { element ->
                !currentKeys.contains(element)
            }
        }
