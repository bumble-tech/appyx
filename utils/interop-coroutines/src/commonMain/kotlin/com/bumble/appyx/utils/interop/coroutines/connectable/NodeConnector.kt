package com.bumble.appyx.utils.interop.coroutines.connectable

import com.bumble.appyx.navigation.lifecycle.Lifecycle
import com.bumble.appyx.navigation.plugin.NodeLifecycleAware
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NodeConnector<Input, Output : Any> : Connectable<Input, Output>, NodeLifecycleAware {

    override fun onCreate(lifecycle: Lifecycle) {
        flushInput()
        flushOutput()
    }

    // region Input
    private fun flushInput() {
        if (!isInputFlushed) {
            val coroutineScope = CoroutineScope(Dispatchers.Default + Job())
            _inputReplayCache.forEach {
                coroutineScope.launch { _input.emit(it) }
            }
        }
        isInputFlushed = true
        _inputReplayCache.clear()
    }

    private var isInputFlushed = false
    private val _input = MutableSharedFlow<Input>()
    private val _inputReplayCache = mutableListOf<Input>()
    override val input: MutableSharedFlow<Input> = object : MutableSharedFlow<Input> {

        override val replayCache: List<Input> = _inputReplayCache

        override val subscriptionCount: StateFlow<Int> = _input.subscriptionCount

        @OptIn(ExperimentalCoroutinesApi::class)
        override fun resetReplayCache() {
            _inputReplayCache.clear()
        }

        override fun tryEmit(value: Input): Boolean =
            if (isInputFlushed) {
                _input.tryEmit(value)
            } else {
                _inputReplayCache.add(value)
            }

        override suspend fun emit(value: Input) {
            if (isInputFlushed) {
                _input.emit(value)
            } else {
                _inputReplayCache.add(value)
            }
        }

        override suspend fun collect(collector: FlowCollector<Input>): Nothing =
            _input.collect(collector)
    }
    // endregion

    // region Output
    private fun flushOutput() {
        if (!isOutputFlushed) {
            val coroutineScope = CoroutineScope(Dispatchers.Default + Job())
            _outputReplayCache.forEach {
                coroutineScope.launch { _output.emit(it) }
            }
        }
        isOutputFlushed = true
        _outputReplayCache.clear()
    }

    private var isOutputFlushed = false
    private val _output = MutableSharedFlow<Output>()
    private val _outputReplayCache = mutableListOf<Output>()
    override val output: MutableSharedFlow<Output> = object : MutableSharedFlow<Output> {

        override val replayCache: List<Output> = _outputReplayCache

        override val subscriptionCount: StateFlow<Int> = _output.subscriptionCount

        @OptIn(ExperimentalCoroutinesApi::class)
        override fun resetReplayCache() {
            _outputReplayCache.clear()
        }

        override fun tryEmit(value: Output): Boolean =
            if (isOutputFlushed) {
                _output.tryEmit(value)
            } else {
                _outputReplayCache.add(value)
            }

        override suspend fun emit(value: Output) {
            if (isOutputFlushed) {
                _output.emit(value)
            } else {
                _outputReplayCache.add(value)
            }
        }

        override suspend fun collect(collector: FlowCollector<Output>): Nothing =
            _output.collect(collector)
    }
    // endregion

}
