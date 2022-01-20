package com.bumble.appyx.v2.core.routing.source.permanent.operation

import com.bumble.appyx.v2.core.routing.Operation

sealed interface PermanentOperation<T> : Operation<T, Int>
