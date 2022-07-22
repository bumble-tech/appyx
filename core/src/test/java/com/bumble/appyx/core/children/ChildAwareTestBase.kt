package com.bumble.appyx.core.children

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.build
import com.bumble.appyx.core.routing.Operation
import com.bumble.appyx.core.routing.RoutingElement
import com.bumble.appyx.core.routing.RoutingElements
import com.bumble.appyx.core.routing.RoutingKey
import com.bumble.appyx.core.routing.RoutingSource
import com.bumble.appyx.core.routing.RoutingSourceAdapter
import com.bumble.appyx.testing.junit4.util.MainDispatcherRule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import org.junit.Before
import org.junit.Rule
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.reflect.KClass

abstract class ChildAwareTestBase {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    protected lateinit var root: Root

    @Before
    open fun before() {
        root = Root().build()
    }

    fun add(vararg key: RoutingKey<Configuration>): List<Node> {
        root.routing.add(*key)
        return root
            .children
            .value
            .values
            .filter { entry -> entry.key in key }
            .sortedBy { it.key.routing }
            .mapNotNull { it.nodeOrNull }
    }

    sealed class Configuration : Comparable<Configuration> {
        abstract val id: Int

        data class Child1(override val id: Int = 0) : Configuration()
        data class Child2(override val id: Int = 0) : Configuration()
        data class Child3(override val id: Int = 0) : Configuration()

        override fun compareTo(other: Configuration): Int {
            val classOrder = arrayOf(Child1::class, Child2::class, Child3::class)
            val compClassOrder =
                classOrder
                    .indexOf(this::class)
                    .compareTo(classOrder.indexOf(other::class))
            return if (compClassOrder != 0) {
                compClassOrder
            } else {
                id.compareTo(other.id)
            }
        }
    }

    class Root(
        val routing: TestRoutingSource<Configuration> = TestRoutingSource(),
        childMode: ChildEntry.ChildMode = ChildEntry.ChildMode.EAGER,
    ) : ParentNode<Configuration>(
        buildContext = BuildContext.root(null),
        routingSource = routing,
        childMode = childMode,
    ) {
        override fun resolve(routing: Configuration, buildContext: BuildContext): Node =
            when (routing) {
                is Configuration.Child1 -> Child1(buildContext)
                is Configuration.Child2 -> Child2(buildContext)
                is Configuration.Child3 -> Child3(buildContext)
            }

        @Composable
        override fun View(modifier: Modifier) {
        }

        fun <T : Node> whenChildAttachedTest(child: KClass<T>, callback: ChildCallback<T>) {
            super.whenChildAttached(child, callback)
        }

        fun <T1 : Node, T2 : Node> whenChildrenAttachedTest(
            child1: KClass<T1>,
            child2: KClass<T2>,
            callback: ChildrenCallback<T1, T2>
        ) {
            super.whenChildrenAttached(child1, child2, callback)
        }
    }

    class Child1(buildContext: BuildContext) : EmptyLeafNode(buildContext)
    class Child2(buildContext: BuildContext) : EmptyLeafNode(buildContext)
    class Child3(buildContext: BuildContext) : EmptyLeafNode(buildContext)

    abstract class EmptyLeafNode(
        buildContext: BuildContext
    ) : Node(buildContext = buildContext) {
        @Composable
        override fun View(modifier: Modifier) {
        }
    }

    class TestRoutingSource<Key> : RoutingSource<Key, Int> {

        private val scope = CoroutineScope(EmptyCoroutineContext + Dispatchers.Unconfined)
        private val state = MutableStateFlow(emptyList<RoutingElement<Key, Int>>())
        override val elements: StateFlow<RoutingElements<Key, Int>>
            get() = state
        override val screenState: StateFlow<RoutingSourceAdapter.ScreenState<Key, out Int>>
            get() = state.map { RoutingSourceAdapter.ScreenState(onScreen = it) }
                .stateIn(scope, SharingStarted.Eagerly, RoutingSourceAdapter.ScreenState())

        override fun onBackPressed() = Unit

        override fun onTransitionFinished(keys: Collection<RoutingKey<Key>>) {
            state.update { list ->
                list.map {
                    if (it.key in keys) {
                        it.onTransitionFinished()
                    } else {
                        it
                    }
                }
            }
        }

        override val canHandleBackPress: StateFlow<Boolean>
            get() = MutableStateFlow(false)

        fun add(vararg key: RoutingKey<Key>) {
            state.update { list ->
                require(list.none { it.key.routing in key.map { routingKey -> routingKey.routing } })
                list + key.map {
                    RoutingElement(
                        key = it,
                        fromState = 0,
                        targetState = 0,
                        operation = Operation.Noop(),
                    )
                }
            }
        }

    }

}
