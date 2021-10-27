package com.github.zsoltk.composeribs.core.builder

import com.github.zsoltk.composeribs.core.Node

abstract class Builder<P> {

    abstract fun build(payload: P): Node<*>
}
