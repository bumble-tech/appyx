package com.bumble.appyx.routingsource.spotlight.operation

import com.bumble.appyx.core.routing.Operation
import com.bumble.appyx.routingsource.spotlight.Spotlight

typealias SpotlightOperation<T> = Operation<T, Spotlight.TransitionState>
