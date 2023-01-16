package com.bumble.appyx.app.node.backstack.app.indexedbackstack.operation

import com.bumble.appyx.app.node.backstack.app.indexedbackstack.IndexedBackStack
import com.bumble.appyx.core.navigation.Operation

interface IndexedBackStackOperation<T> : Operation<T, IndexedBackStack.State>
