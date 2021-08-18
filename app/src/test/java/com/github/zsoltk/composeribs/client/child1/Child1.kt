package com.github.zsoltk.composeribs.client.child1

interface Child1 {

    interface Dependency

    sealed class Routing {
        object GrandChild1 : Routing()
        object GrandChild2 : Routing()
    }
}


