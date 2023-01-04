package com.bumble.appyx.core.children

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.Appyx
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.navigation.BaseNavModel
import com.bumble.appyx.core.navigation.NavElement
import com.bumble.appyx.core.navigation.NavElements
import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.core.navigation.Operation
import com.bumble.appyx.core.navigation.backpresshandlerstrategies.DontHandleBackPress
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.build
import com.bumble.appyx.testing.junit4.util.MainDispatcherRule
import org.junit.Before
import org.junit.Rule
import kotlin.reflect.KClass

open class ChildAwareTestBase {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    protected lateinit var root: Root

    @Before
    open fun before() {
        root = Root().build()
    }

    fun add(vararg key: NavKey<Configuration>): List<Node> =
        add(visible = true, key = key)

    fun add(visible: Boolean, vararg key: NavKey<Configuration>): List<Node> {
        root.testNavModel.add(visible = visible, key = key)
        return root.children(key = key)
    }

    fun move(visible: Boolean, vararg key: NavKey<Configuration>): List<Node> {
        root.testNavModel.changeState(visible = visible, *key)
        return root
            .children
            .value
            .values
            .filter { entry -> entry.key in key }
            .sortedBy { it.key.navTarget }
            .mapNotNull { it.nodeOrNull }
    }

    fun destroy(vararg key: NavKey<Configuration>) {
        root.testNavModel.destroy(*key)
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
        val testNavModel: Pool<Configuration> = Pool(),
        val mode: ChildEntry.KeepMode = Appyx.defaultChildKeepMode,
    ) : ParentNode<Configuration>(
        buildContext = BuildContext.root(null),
        childKeepMode = mode,
        navModel = testNavModel,
    ) {
        override fun resolve(navTarget: Configuration, buildContext: BuildContext): Node =
            when (navTarget) {
                is Configuration.Child1 -> Child1(buildContext)
                is Configuration.Child2 -> Child2(buildContext)
                is Configuration.Child3 -> Child3(buildContext)
            }

        @Composable
        override fun View(modifier: Modifier) {
            // no-op
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

        fun children(vararg key: NavKey<Configuration>): List<Node> =
            children
                .value
                .values
                .filter { entry -> entry.key in key }
                .sortedBy { it.key.navTarget }
                .mapNotNull { it.nodeOrNull }

    }

    class Child1(buildContext: BuildContext) : EmptyLeafNode(buildContext)
    class Child2(buildContext: BuildContext) : EmptyLeafNode(buildContext)
    class Child3(buildContext: BuildContext) : EmptyLeafNode(buildContext)

    abstract class EmptyLeafNode(
        buildContext: BuildContext
    ) : Node(buildContext = buildContext) {
        @Composable
        override fun View(modifier: Modifier) {
            // no-op
        }
    }

    class Pool<NavTarget : Any> : BaseNavModel<NavTarget, Pool.State>(
        backPressHandler = DontHandleBackPress(),
        screenResolver = { state -> state == State.VISIBLE },
        finalState = State.DESTROYED,
        savedStateMap = null,
    ) {
        override val initialElements: NavElements<NavTarget, State> get() = emptyList()

        fun add(visible: Boolean, vararg key: NavKey<NavTarget>) {
            val state = if (visible) State.VISIBLE else State.STASHED
            updateState { elements ->
                elements + key.map {
                    NavElement(
                        key = it,
                        fromState = state,
                        targetState = state,
                        operation = Operation.Noop(),
                    )
                }
            }
        }

        fun changeState(visible: Boolean, vararg key: NavKey<NavTarget>) {
            val state = if (visible) State.VISIBLE else State.STASHED
            updateState { elements ->
                elements.map {
                    if (it.key in key && it.targetState != state) {
                        it.transitionTo(state, Operation.Noop()).onTransitionFinished()
                    } else {
                        it
                    }
                }
            }
        }

        fun destroy(vararg key: NavKey<NavTarget>) {
            updateState { elements ->
                elements.map {
                    if (it.key in key && it.targetState != State.DESTROYED) {
                        it.transitionTo(State.DESTROYED, Operation.Noop()).onTransitionFinished()
                    } else {
                        it
                    }
                }
            }
        }

        enum class State {
            VISIBLE, STASHED, DESTROYED
        }
    }

}
