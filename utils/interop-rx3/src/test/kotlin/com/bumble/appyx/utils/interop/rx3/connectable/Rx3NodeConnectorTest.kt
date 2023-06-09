package com.bumble.appyx.utils.interop.rx3.connectable

import androidx.lifecycle.Lifecycle
import com.bumble.appyx.navigation.lifecycle.CommonLifecycle
import com.bumble.appyx.navigation.lifecycle.DefaultPlatformLifecycleObserver
import com.bumble.appyx.navigation.lifecycle.PlatformLifecycleObserver
import com.bumble.appyx.navigation.platform.toCommonState
import com.bumble.appyx.utils.interop.rx3.connectable.Rx3NodeConnectorTest.Output.Output1
import com.bumble.appyx.utils.interop.rx3.connectable.Rx3NodeConnectorTest.Output.Output2
import com.bumble.appyx.utils.interop.rx3.connectable.Rx3NodeConnectorTest.Output.Output3
import io.reactivex.rxjava3.observers.TestObserver
import kotlinx.coroutines.CoroutineScope
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.EmptyCoroutineContext

internal class Rx3NodeConnectorTest {

    private val firstTestObserver = TestObserver<Output>()
    private val secondTestObserver = TestObserver<Output>()
    private var lifecycleState = Lifecycle.State.CREATED

    private val lifecycle = object : CommonLifecycle {

        override val currentState: CommonLifecycle.State
            get() = lifecycleState.toCommonState()

        override val coroutineScope: CoroutineScope = CoroutineScope(EmptyCoroutineContext)

        override fun addObserver(observer: PlatformLifecycleObserver) {
            if (lifecycleState == Lifecycle.State.CREATED) {
                (observer as DefaultPlatformLifecycleObserver).onCreate()
            }
        }

        override fun removeObserver(observer: PlatformLifecycleObserver) {
            // NO-OP
        }
    }

    sealed class Output {
        object Output1 : Output()
        object Output2 : Output()
        object Output3 : Output()
    }

    @AfterEach
    fun tearDown() {
        firstTestObserver.dispose()
        secondTestObserver.dispose()
    }

    @Test
    fun `GIVEN nodeConnector onAttached is not called WHEN output is accepted THEN accepted output do not reach observer`() {
        val nodeConnector = NodeConnector<Nothing, Output>()
        nodeConnector.output.subscribe(firstTestObserver)

        nodeConnector.output.accept(Output1)

        firstTestObserver.assertValueCount(0)
    }

    @Test
    fun `GIVEN an output is accepted before onAttached WHEN nodeConnector onAttached is called THEN accepted output reach the observer`() {
        val nodeConnector = NodeConnector<Nothing, Output>()
        nodeConnector.output.subscribe(firstTestObserver)

        nodeConnector.output.accept(Output1)
        nodeConnector.onCreate(lifecycle)

        firstTestObserver.assertValues(Output1)
    }

    @Test
    fun `GIVEN nodeConnector is attached WHEN output is accepted THEN every accepted output reach the observer`() {
        val nodeConnector = NodeConnector<Nothing, Output>()
        nodeConnector.output.subscribe(firstTestObserver)

        nodeConnector.onCreate(lifecycle)
        nodeConnector.output.accept(Output1)

        firstTestObserver.assertValues(Output1)
    }

    @Test
    fun `GIVEN outputs accepted before and after onAttached WHEN node is attached THEN every accepted output reach the observer`() {
        val nodeConnector = NodeConnector<Nothing, Output>()
        nodeConnector.output.subscribe(firstTestObserver)

        nodeConnector.output.accept(Output1)
        nodeConnector.onCreate(lifecycle)
        nodeConnector.output.accept(Output2)
        nodeConnector.output.accept(Output3)

        firstTestObserver.assertValues(Output1, Output2, Output3)
    }

    @Test
    fun `WHEN nodeConnector onAttached is called twice THEN error is raised`() {
        val nodeConnector = NodeConnector<Nothing, Output>()

        nodeConnector.onCreate(lifecycle)
        assertThrows(IllegalStateException::class.java) {
            nodeConnector.onCreate(lifecycle)
        }
    }

    @Test
    fun `GIVEN multiple observers and output is accepted before OnAttached WHEN nodeConnector onAttached is called THEN every accepted output reach the observers`() {
        val nodeConnector = NodeConnector<Nothing, Output>()
        nodeConnector.output.subscribe(firstTestObserver)
        nodeConnector.output.subscribe(secondTestObserver)

        nodeConnector.output.accept(Output1)
        nodeConnector.onCreate(lifecycle)

        firstTestObserver.assertValues(Output1)
        secondTestObserver.assertValues(Output1)
    }

    @Test
    fun `GIVEN multiple observers and nodeConnector is attached WHEN output is accepted THEN every accepted output reach the observer`() {
        val nodeConnector = NodeConnector<Nothing, Output>()
        nodeConnector.output.subscribe(firstTestObserver)
        nodeConnector.output.subscribe(secondTestObserver)

        nodeConnector.onCreate(lifecycle)
        nodeConnector.output.accept(Output1)

        firstTestObserver.assertValues(Output1)
        secondTestObserver.assertValues(Output1)
    }

    @Test
    fun `GIVEN multiple observers that subscribe before and after onAttached  and outputs accepted before and after onAttached WHEN node is attached THEN every accepted output reach the observer`() {
        val nodeConnector = NodeConnector<Nothing, Output>()
        //First subscriber subscribe BEFORE onAttached
        nodeConnector.output.subscribe(firstTestObserver)

        //Output accepted BEFORE onAttached
        nodeConnector.output.accept(Output1)
        nodeConnector.onCreate(lifecycle)

        //Second subscriber subscribe AFTER onAttached
        nodeConnector.output.subscribe(secondTestObserver)

        //Outputs accepted AFTER onAttached
        nodeConnector.output.accept(Output2)
        nodeConnector.output.accept(Output3)

        firstTestObserver.assertValues(Output1, Output2, Output3)
        secondTestObserver.assertValues(Output2, Output3)

    }


    @Test
    fun `WHEN multiple output are accepted from multiple threads THEN output is correctly received when onAttached is called`() {
        val nodeConnector = NodeConnector<Nothing, Output>()
        val threadNumber = 100
        val iterations = 10000
        val barrier = CyclicBarrier(threadNumber + 1)
        val executor = Executors.newFixedThreadPool(threadNumber).apply {
            repeat(threadNumber) {
                submit {
                    barrier.awaitWithTimeOut()
                    repeat(iterations) {
                        nodeConnector.output.accept(Output1)
                        nodeConnector.output.accept(Output2)
                        nodeConnector.output.accept(Output3)

                    }
                }
            }
        }

        //Unlock threads(trip barrier) and wait for them to complete
        barrier.awaitWithTimeOut()
        executor.shutdown()
        executor.awaitWithTimeOut()

        nodeConnector.output.subscribe(firstTestObserver)
        nodeConnector.onCreate(lifecycle)

        firstTestObserver.assertValueCount(threadNumber * iterations * 3)
    }


    /**
     * Why is this test repeated 1000 times?
     * This test exist to ensure that there is no race condition issues under the threading scenario described by the test.
     * To do so, a high amount of repetitions must be executed as when the race condition is happening, it could only produce
     * an unexpected result in some % of the case.
     * E.g: Race condition between Thread1 and Thread2
     * When Thread1 is executed first -> Desired Scenario
     * When Thread2 is executed first -> Undesired Scenario
     * Under this example if both threads get the lock with the same priority, the % of each case would be 50%.
     *
     * Why is the test logic duplicated?
     * Seems that CyclicBarrier is a bit biased and is giving preference to the first thread awaiting to be executed first.
     * So to balance this weighed thread execution priority, we switch order and test both in the same test to increase the
     * % of failure when race condition issue is present.
     */
    @RepeatedTest(1000)
    fun `WHEN accept and onAttached are called by different thread at the same time THEN output is the expected`() {
        val nodeConnector1 = NodeConnector<Nothing, Output>()
        val nodeConnector2 = NodeConnector<Nothing, Output>()
        val threadNumber = 2
        val barrier1 = CyclicBarrier(threadNumber + 1)
        val barrier2 = CyclicBarrier(threadNumber + 1)

        val executor = Executors.newFixedThreadPool(threadNumber).apply {
            //Emitter thread
            submit {
                barrier1.awaitWithTimeOut()
                nodeConnector1.output.accept(Output1)
            }
            //Attacher thread
            submit {
                barrier1.awaitWithTimeOut()
                nodeConnector1.onCreate(lifecycle)
            }
        }
        val executor2 = Executors.newFixedThreadPool(threadNumber).apply {
            //Attacher thread
            submit {
                barrier2.awaitWithTimeOut()
                nodeConnector2.onCreate(lifecycle)
            }
            //Emitter thread
            submit {
                barrier2.awaitWithTimeOut()
                nodeConnector2.output.accept(Output1)
            }
        }

        //subscribe nodes
        nodeConnector1.output.subscribe(firstTestObserver)
        nodeConnector2.output.subscribe(secondTestObserver)

        //Unlock threads(trip barrier) and wait for them to complete
        barrier1.awaitWithTimeOut()
        barrier2.awaitWithTimeOut()
        executor.shutdown()
        executor2.shutdown()
        executor.awaitWithTimeOut()
        executor2.awaitWithTimeOut()

        firstTestObserver.assertValues(Output1)
        secondTestObserver.assertValues(Output1)
    }


    private fun CyclicBarrier.awaitWithTimeOut() {
        await(TIME_OUT_S, TimeUnit.SECONDS)
    }

    private fun ExecutorService.awaitWithTimeOut() {
        awaitTermination(TIME_OUT_S, TimeUnit.SECONDS)
    }

    companion object {
        private const val TIME_OUT_S = 30L
    }
}
