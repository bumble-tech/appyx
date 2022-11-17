package com.bumble.appyx.testing.unit.common.util

import com.bumble.appyx.core.navigation.EmptyNavModel

@Deprecated(
    message = "Replaced with to EmptyNavModel",
    replaceWith = ReplaceWith(
        expression = "EmptyNavModel",
        imports = ["com.bumble.appyx.core.navigation.EmptyNavModel"]
    ),
)
typealias DummyNavModel<NavTarget, State> = EmptyNavModel<NavTarget, State>
