package com.github.zsoltk.composeribs.client.container

import com.github.zsoltk.composeribs.client.child1.Child1
import com.github.zsoltk.composeribs.client.child1.Child1Builder
import com.github.zsoltk.composeribs.client.child2.Child2Builder
import com.github.zsoltk.composeribs.client.child2.Child2

class ContainerChildBuilders {

    private val subtreeDependency = SubtreeDependency()

    val child1Builder = Child1Builder(subtreeDependency)
    val child2Builder = Child2Builder(subtreeDependency)

    class SubtreeDependency() : Child1.Dependency, Child2.Dependency {

    }

}
