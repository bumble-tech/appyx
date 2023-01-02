package com.bumble.appyx.core.navigation.model.permanent.operation

import android.os.Parcelable
import com.bumble.appyx.core.navigation.EmptyState
import com.bumble.appyx.core.navigation.Operation

interface PermanentOperation<T : Parcelable> : Operation<T, EmptyState>
