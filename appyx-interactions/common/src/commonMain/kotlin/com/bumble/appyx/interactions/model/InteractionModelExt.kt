package com.bumble.appyx.interactions.model

import com.bumble.appyx.withPrevious
import kotlinx.coroutines.flow.map

fun <InteractionTarget : Any, ModelState : Any> AppyxComponent<InteractionTarget, ModelState>.removedElements() =
    this
        .elements
        .withPrevious()
        .map { values ->
            val previousKeys = values.previous?.all.orEmpty()
            val currentKeys = values.current.all
            previousKeys.filter { element ->
                !currentKeys.contains(element)
            }
        }
