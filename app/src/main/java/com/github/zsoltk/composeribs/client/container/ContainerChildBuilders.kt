package com.github.zsoltk.composeribs.client.container

import com.github.zsoltk.composeribs.client.child1.Child1
import com.github.zsoltk.composeribs.client.child1.Child1Builder
import com.github.zsoltk.composeribs.client.child2.Child2
import com.github.zsoltk.composeribs.client.child2.Child2Builder
import com.github.zsoltk.composeribs.client.childn.ChildBuilder
import com.github.zsoltk.composeribs.client.childn.Child

class ContainerChildBuilders {

    private val subtreeDependency = SubtreeDependency()

    val childBuilder = ChildBuilder(subtreeDependency)
    val child1Builder = Child1Builder(subtreeDependency)
    val child2Builder = Child2Builder(subtreeDependency)

    class SubtreeDependency() :
        Child.Dependency,
        Child1.Dependency,
        Child2.Dependency {

    }

}
