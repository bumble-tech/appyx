package com.bumble.appyx.routingsourcedemos.spotlight.operation

import com.bumble.appyx.core.routing.Operation
import com.bumble.appyx.routingsourcedemos.spotlight.Spotlight

typealias SpotlightOperation<T> = Operation<T, Spotlight.TransitionState>
