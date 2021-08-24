package com.github.zsoltk.composeribs.client.container

interface Container {

    interface Dependency

    sealed class Routing {
        data class Child(val i: Int) : Routing()
        object Child1 : Routing()
        object Child2 : Routing()
        object Child3 : Routing()
        object Child4 : Routing()
    }
}


