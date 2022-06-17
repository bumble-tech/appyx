package com.bumble.appyx.core.routing.source.spotlight.operation

import com.bumble.appyx.core.routing.Operation
import com.bumble.appyx.core.routing.source.spotlight.Spotlight

typealias SpotlightOperation<T> = Operation<T, Spotlight.TransitionState>
