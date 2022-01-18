package com.github.zsoltk.composeribs.core.routing.source.permanent.operation

import com.github.zsoltk.composeribs.core.routing.Operation

sealed interface PermanentOperation<T> : Operation<T, Int>
