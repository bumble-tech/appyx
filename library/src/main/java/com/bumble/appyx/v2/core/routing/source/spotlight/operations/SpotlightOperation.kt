package com.bumble.appyx.v2.core.routing.source.spotlight.operations

import com.bumble.appyx.v2.core.routing.Operation
import com.bumble.appyx.v2.core.routing.source.spotlight.Spotlight

typealias SpotlightOperation<T> = Operation<T, Spotlight.TransitionState>
