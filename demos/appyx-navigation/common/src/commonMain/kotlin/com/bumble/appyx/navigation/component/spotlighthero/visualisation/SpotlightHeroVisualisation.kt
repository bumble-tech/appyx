package com.bumble.appyx.navigation.component.spotlighthero.visualisation

import com.bumble.appyx.interactions.core.ui.Visualisation
import com.bumble.appyx.navigation.component.spotlighthero.SpotlightHeroModel
import com.bumble.appyx.navigation.component.spotlighthero.visualisation.property.HeroProgress

interface SpotlightHeroVisualisation<InteractionTarget : Any> :
    Visualisation<InteractionTarget, SpotlightHeroModel.State<InteractionTarget>> {

    val heroProgress: HeroProgress
}
