package com.bumble.appyx.navigation.component.spotlighthero.visualisation

import com.bumble.appyx.interactions.core.ui.Visualisation
import com.bumble.appyx.navigation.component.spotlighthero.SpotlightHeroModel
import com.bumble.appyx.navigation.component.spotlighthero.visualisation.property.HeroProgress

interface SpotlightHeroVisualisation<NavTarget : Any> :
    Visualisation<NavTarget, SpotlightHeroModel.State<NavTarget>> {

    val heroProgress: HeroProgress
}
