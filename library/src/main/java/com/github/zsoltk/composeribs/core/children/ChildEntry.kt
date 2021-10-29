package com.github.zsoltk.composeribs.core.children

import com.github.zsoltk.composeribs.core.Node
import com.github.zsoltk.composeribs.core.modality.BuildContext
import com.github.zsoltk.composeribs.core.routing.Resolver
import com.github.zsoltk.composeribs.core.routing.RoutingKey

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
        val node: Node<*>
    ) : ChildEntry<T>()

    /** Internal child representation for Lazy mode. */
    internal class Lazy<T>(
        override val key: RoutingKey<T>,
        private val resolver: Resolver<T>,
        val buildContext: BuildContext,
    ) : ChildEntry<T>() {
        fun initialize(): Eager<T> =
            Eager(key, resolver.resolve(key.routing, buildContext))
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
                    Eager(key, resolver.resolve(key.routing, buildContext))
                ChildMode.LAZY ->
                    Lazy(key, resolver, buildContext)
            }
    }

}
