package com.github.zsoltk.composeribs.core.routing.source.spotlight.operations

import com.github.zsoltk.composeribs.core.routing.Operation
import com.github.zsoltk.composeribs.core.routing.source.spotlight.Spotlight

typealias SpotlightOperation<T> = Operation<T, Spotlight.TransitionState>