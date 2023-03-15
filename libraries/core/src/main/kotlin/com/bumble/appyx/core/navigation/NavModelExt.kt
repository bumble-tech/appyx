package com.bumble.appyx.core.navigation

import com.bumble.appyx.core.withPrevious
import kotlinx.coroutines.flow.map

fun <NavTarget, State> NavModel<NavTarget, State>.removedElementKeys() =
    this
        .elements
        .withPrevious()
        .map { values ->
            val previousKeys = values.previous?.map { it.key }
            val currentKeys = values.current.map { it.key }
            if (previousKeys == null) {
                emptyList()
            } else {
                val deletedElements = mutableListOf<NavKey<NavTarget>>()
                previousKeys.forEach { element ->
                    if (currentKeys.contains(element).not()) {
                        deletedElements.add(element)
                    }
                }
                deletedElements
            }
        }
