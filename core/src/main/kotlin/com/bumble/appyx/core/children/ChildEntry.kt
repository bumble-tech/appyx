package com.bumble.appyx.core.children

import com.bumble.appyx.core.navigation.RoutingKey
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.state.SavedStateMap

// custom equals/hashCode for MutableStateFlow and equality checks
sealed class ChildEntry<T> {
    abstract val key: RoutingKey<T>

    override fun equals(other: Any?): Boolean =
        other?.javaClass == javaClass && (other as? ChildEntry<*>)?.key == key

    override fun hashCode(): Int =
        key.hashCode()

    override fun toString(): String =
        "$key@${javaClass.simpleName}"

    /** All public APIs should return this type of child which is ready to work with. */
    class Initialized<T>(
        override val key: RoutingKey<T>,
        val node: Node,
    ) : ChildEntry<T>()

    class Suspended<T>(
        override val key: RoutingKey<T>,
        val savedState: SavedStateMap?,
    ) : ChildEntry<T>()

    /** When to create child nodes? */
    enum class ChildMode {

        /** When routing state was changed. */
        EAGER,

        /** When node rendering was requested. */
        LAZY,

    }

    /** Keep not on screen nodes in the memory? */
    enum class KeepMode {
        KEEP,
        SUSPEND,
    }

}
