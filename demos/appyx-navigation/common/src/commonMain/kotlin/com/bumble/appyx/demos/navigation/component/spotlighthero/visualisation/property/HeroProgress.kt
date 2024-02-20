package com.bumble.appyx.demos.navigation.component.spotlighthero.visualisation.property

import com.bumble.appyx.interactions.ui.property.impl.GenericFloatProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HeroProgress(
    coroutineScope: CoroutineScope,
    target: Target,
    displacement: StateFlow<Float> = MutableStateFlow(0f),
) : GenericFloatProperty(
    coroutineScope = coroutineScope,
    target = target,
    displacement = displacement
)
