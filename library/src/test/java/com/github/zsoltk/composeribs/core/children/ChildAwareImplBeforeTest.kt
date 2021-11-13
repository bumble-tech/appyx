package com.github.zsoltk.composeribs.core.children

import com.github.zsoltk.composeribs.core.node.Node
import kotlin.reflect.KClass

class ChildAwareImplBeforeTest : ChildAwareTestBase(), ChildAwareCommonTestSpec {

    override val registerBefore: Boolean = true

    override fun <T : Node> whenChildAttached(
        klass: KClass<T>,
        callback: ChildCallback<T>
    ) {
        root.whenChildAttached(klass, callback)
    }

    override fun <T1 : Node, T2 : Node> whenChildrenAttached(
        klass1: KClass<T1>,
        klass2: KClass<T2>,
        callback: ChildrenCallback<T1, T2>
    ) {
        root.whenChildrenAttached(klass1, klass2, callback)
    }

}
