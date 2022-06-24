package com.bumble.appyx.core.children

import com.bumble.appyx.core.node.Node
import kotlin.reflect.KClass

class ChildAwareImplAfterTest : ChildAwareTestBase(), ChildAwareCommonTestSpec {

    override val registerBefore: Boolean = false

    override fun <T : Node> whenChildAttached(
        klass: KClass<T>,
        callback: ChildCallback<T>
    ) {
        root.whenChildAttachedTest(klass, callback)
    }

    override fun <T1 : Node, T2 : Node> whenChildrenAttached(
        klass1: KClass<T1>,
        klass2: KClass<T2>,
        callback: ChildrenCallback<T1, T2>
    ) {
        root.whenChildrenAttachedTest(klass1, klass2, callback)
    }

}
