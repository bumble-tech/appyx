package com.bumble.appyx.demos.navigation.component.spotlighthero.visualisation

import com.bumble.appyx.demos.navigation.component.spotlighthero.SpotlightHeroModel
import com.bumble.appyx.demos.navigation.component.spotlighthero.visualisation.property.HeroProgress
import com.bumble.appyx.interactions.core.ui.Visualisation

interface SpotlightHeroVisualisation<NavTarget : Any> :
    Visualisation<NavTarget, SpotlightHeroModel.State<NavTarget>> {

    val heroProgress: HeroProgress
}
