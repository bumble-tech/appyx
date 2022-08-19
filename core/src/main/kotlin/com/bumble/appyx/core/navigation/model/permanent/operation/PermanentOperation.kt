package com.bumble.appyx.core.navigation.model.permanent.operation

import com.bumble.appyx.core.navigation.Operation

sealed interface PermanentOperation<T> : Operation<T, Int>
