package com.bumble.appyx.navigation.children

import com.bumble.appyx.interactions.core.Element
import com.bumble.appyx.navigation.node.AbstractNode
import com.bumble.appyx.utils.multiplatform.SavedStateMap

// custom equals/hashCode for MutableStateFlow and equality checks
sealed class ChildEntry<T> {
    abstract val key: Element<T>

    override fun equals(other: Any?): Boolean =
        other != null
                && other::class == this::class
                && (other as? ChildEntry<*>)?.key == key

    override fun hashCode(): Int =
        key.hashCode()

    override fun toString(): String =
        "$key@${this::class.simpleName}"

    /** All public APIs should return this type of child which is ready to work with. */
    class Initialized<T>(
        override val key: Element<T>,
        val node: AbstractNode,
    ) : ChildEntry<T>()

    class Suspended<T>(
        override val key: Element<T>,
        val savedState: SavedStateMap?,
    ) : ChildEntry<T>()

    /** Keep off-screen nodes in the memory? */
    enum class KeepMode {
        KEEP,
        SUSPEND,
    }

}
