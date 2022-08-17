package com.bumble.appyx.core.children

import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.build
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.navigation.Resolver
import com.bumble.appyx.core.navigation.RoutingKey

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
    class Eager<T>(
        override val key: RoutingKey<T>,
        val node: Node,
    ) : ChildEntry<T>()

    /** Child representation for Lazy mode. */
    class Lazy<T>(
        override val key: RoutingKey<T>,
        private val resolver: Resolver<T>,
        val buildContext: BuildContext,
    ) : ChildEntry<T>() {
        internal fun initialize(): Eager<T> =
            Eager(key, resolver.resolve(key.routing, buildContext).build())
    }

    /** When to create child nodes? */
    enum class ChildMode {

        /** When routing state was changed. */
        EAGER,

        /** When node rendering was requested. */
        LAZY,

    }

    companion object {
        fun <T> create(
            key: RoutingKey<T>,
            resolver: Resolver<T>,
            buildContext: BuildContext,
            childMode: ChildMode,
        ): ChildEntry<T> =
            when (childMode) {
                ChildMode.EAGER ->
                    Eager(key, resolver.resolve(key.routing, buildContext).build())
                ChildMode.LAZY ->
                    Lazy(key, resolver, buildContext)
            }
    }

}
