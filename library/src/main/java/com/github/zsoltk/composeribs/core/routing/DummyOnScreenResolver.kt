package com.github.zsoltk.composeribs.core.routing

import kotlinx.parcelize.Parcelize

@Parcelize
internal object DummyOnScreenResolver : OnScreenResolver<Any> {
    override fun resolve(state: Any): Boolean = true
}
