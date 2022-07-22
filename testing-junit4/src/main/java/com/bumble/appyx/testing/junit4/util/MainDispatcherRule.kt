package com.bumble.appyx.testing.junit4.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }

}
