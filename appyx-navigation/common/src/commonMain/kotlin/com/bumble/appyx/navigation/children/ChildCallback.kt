package com.bumble.appyx.navigation.children

import com.bumble.appyx.navigation.lifecycle.CommonLifecycle

typealias ChildrenCallback<T1, T2> = (commonLifecycle: CommonLifecycle, child1: T1, child2: T2) -> Unit

typealias ChildCallback<T> = (commonLifecycle: CommonLifecycle, child: T) -> Unit
