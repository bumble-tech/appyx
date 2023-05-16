package com.bumble.appyx.navigation.children

import androidx.lifecycle.Lifecycle

typealias ChildrenCallback<T1, T2> = (commonLifecycle: Lifecycle, child1: T1, child2: T2) -> Unit

typealias ChildCallback<T> = (commonLifecycle: Lifecycle, child: T) -> Unit
