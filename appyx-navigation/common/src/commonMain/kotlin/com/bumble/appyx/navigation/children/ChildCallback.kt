package com.bumble.appyx.navigation.children

import com.bumble.appyx.navigation.lifecycle.Lifecycle

typealias ChildrenCallback<T1, T2> = (lifecycle: Lifecycle, child1: T1, child2: T2) -> Unit

typealias ChildCallback<T> = (lifecycle: Lifecycle, child: T) -> Unit
