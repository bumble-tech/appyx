package com.bumble.appyx.navigation.node.backstack.app.indexedbackstack.operation

import com.bumble.appyx.navigation.node.backstack.app.indexedbackstack.IndexedBackStack
import com.bumble.appyx.core.navigation.Operation

interface IndexedBackStackOperation<T> : Operation<T, IndexedBackStack.State>
