package com.bumble.appyx.app.node.backstack.app.indexedbackstack.operation

import android.os.Parcelable
import com.bumble.appyx.app.node.backstack.app.indexedbackstack.IndexedBackStack
import com.bumble.appyx.core.navigation.Operation

interface IndexedBackStackOperation<T : Parcelable> : Operation<T, IndexedBackStack.State>
