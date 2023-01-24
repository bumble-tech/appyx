package com.bumble.appyx.navigation.children

import com.bumble.appyx.interactions.core.NavElement
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.state.SavedStateMap

// custom equals/hashCode for MutableStateFlow and equality checks
sealed class ChildEntry<T> {
    abstract val key: NavElement<T>

    override fun equals(other: Any?): Boolean =
        other?.javaClass == javaClass && (other as? ChildEntry<*>)?.key == key

    override fun hashCode(): Int =
        key.hashCode()

    override fun toString(): String =
        "$key@${javaClass.simpleName}"

    /** All public APIs should return this type of child which is ready to work with. */
    class Initialized<T>(
        override val key: NavElement<T>,
        val node: Node,
    ) : ChildEntry<T>()

    class Suspended<T>(
        override val key: NavElement<T>,
        val savedState: SavedStateMap?,
    ) : ChildEntry<T>()

    /** Keep off-screen nodes in the memory? */
    enum class KeepMode {
        KEEP,
        SUSPEND,
    }

}
