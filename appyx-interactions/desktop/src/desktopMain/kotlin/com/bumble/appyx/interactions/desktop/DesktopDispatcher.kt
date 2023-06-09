package com.bumble.appyx.interactions.desktop

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.internal.MainDispatcherFactory
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext

@OptIn(InternalCoroutinesApi::class)
internal class DesktopDispatcherFactory : MainDispatcherFactory {
    override val loadPriority: Int
        get() = 0

    override fun createDispatcher(allFactories: List<MainDispatcherFactory>): MainCoroutineDispatcher =
        object : MainCoroutineDispatcher() {
            override val immediate: MainCoroutineDispatcher
                get() = this

            override fun dispatch(context: CoroutineContext, block: Runnable) {
                runBlocking {
                    block.run()
                }
            }
        }
}
