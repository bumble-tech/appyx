package com.bumble.appyx.core.routing.source.permanent.operation

import com.bumble.appyx.core.routing.Operation

sealed interface PermanentOperation<T> : Operation<T, Int>
