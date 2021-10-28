package com.github.zsoltk.composeribs.core.children

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.runtime.Composable
import com.github.zsoltk.composeribs.core.LeafNode
import com.github.zsoltk.composeribs.core.Node
import com.github.zsoltk.composeribs.core.SavedStateMap
import com.github.zsoltk.composeribs.core.routing.RoutingElement
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import com.github.zsoltk.composeribs.core.testutils.MainDispatcherRule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.junit.Before
import org.junit.Rule

abstract class ChildAwareTestBase {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    protected lateinit var root: Root

    @Before
    open fun before() {
        root = Root()
    }

    fun add(vararg key: Configuration): List<Node<*>> {
        root.routing.add(*key)
        return root
            .children
            .value
            .values
            .filter { it.key.routing in key }
            .mapNotNull { (it as? ChildEntry.Eager)?.node }
    }

    sealed class Configuration {
        data class Child1(val id: Int = 0) : Configuration()
        data class Child2(val id: Int = 0) : Configuration()
        data class Child3(val id: Int = 0) : Configuration()
    }

    class Root(
        val routing: TestRoutingSource<Configuration> = TestRoutingSource(),
        childMode: ChildEntry.ChildMode = ChildEntry.ChildMode.EAGER,
    ) : Node<Configuration>(
        savedStateMap = null,
        routingSource = routing,
        childMode = childMode,
    ) {
        override fun resolve(routing: Configuration, savedStateMap: SavedStateMap?): Node<*> =
            when (routing) {
                is Configuration.Child1 -> Child1()
                is Configuration.Child2 -> Child2()
                is Configuration.Child3 -> Child3()
            }

        @Composable
        override fun View() {
        }
    }

    class Child1 : EmptyLeafNode()
    class Child2 : EmptyLeafNode()
    class Child3 : EmptyLeafNode()

    abstract class EmptyLeafNode : LeafNode(savedStateMap = null) {
        @Composable
        override fun View() {
        }
    }

    class TestRoutingSource<Key> : RoutingSource<Key, Int> {

        data class RoutingKeyImpl<Key>(
            override val routing: Key,
        ) : RoutingKey<Key>

        private val state = MutableStateFlow(emptyList<RoutingElement<Key, Int>>())
        override val all: StateFlow<List<RoutingElement<Key, Int>>>
            get() = state
        override val onScreen: StateFlow<List<RoutingElement<Key, Int>>>
            get() = all
        override val offScreen: StateFlow<List<RoutingElement<Key, Int>>>
            get() = MutableStateFlow(emptyList())
        override val canHandleBackPress: StateFlow<Boolean>
            get() = MutableStateFlow(false)

        fun add(vararg key: Key) {
            state.update { list ->
                require(list.none { it.key.routing in key })
                list + key.map {
                    RoutingElement(
                        RoutingKeyImpl(it),
                        fromState = 0,
                        targetState = 0
                    )
                }
            }
        }

        override fun onBackPressed() {
        }

        override fun onTransitionFinished(key: RoutingKey<Key>) {
        }

    }

}
