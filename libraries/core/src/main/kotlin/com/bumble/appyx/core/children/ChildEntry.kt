package com.bumble.appyx.core.children

import com.bumble.appyx.core.children.ChildEntry.ChildTransitionStrategy.COMPLETE
import com.bumble.appyx.core.children.ChildEntry.ChildTransitionStrategy.KEEP
import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.state.SavedStateMap

// custom equals/hashCode for MutableStateFlow and equality checks
sealed class ChildEntry<T> {
    abstract val key: NavKey<T>

    override fun equals(other: Any?): Boolean =
        other?.javaClass == javaClass && (other as? ChildEntry<*>)?.key == key

    override fun hashCode(): Int =
        key.hashCode()

    override fun toString(): String =
        "$key@${javaClass.simpleName}"

    /** All public APIs should return this type of child which is ready to work with. */
    class Initialized<T>(
        override val key: NavKey<T>,
        val node: Node,
    ) : ChildEntry<T>()

    class Suspended<T>(
        override val key: NavKey<T>,
        val savedState: SavedStateMap?,
    ) : ChildEntry<T>()

    /** Keep off-screen nodes in the memory? */
    enum class KeepMode {
        KEEP,
        SUSPEND,
    }

    /**
     * How should pending Child transitions be handled if the Child leaves the composition?
     * [KEEP] will suspend the state transition until the child re-enters composition
     * [COMPLETE] will complete the transition as soon as the child leaves the composition,
     * to avoid leaving incomplete state that might cause visual or state bugs when using eg a LazyList
     * [KEEP] is the original behaviour, [COMPLETE] is the proposed replacement behaviour.
     * This is configurable in case [KEEP] behaviour is required in an existing use-case.
     * */
    enum class ChildTransitionStrategy {
        KEEP,
        COMPLETE,
    }
}
