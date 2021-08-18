package com.github.zsoltk.composeribs.client.container

interface Container {

    interface Dependency

    sealed class Routing {
        object Child1 : Routing()
        object Child2 : Routing()
    }
}


