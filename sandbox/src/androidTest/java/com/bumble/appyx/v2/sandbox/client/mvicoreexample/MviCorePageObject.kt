package com.bumble.appyx.v2.sandbox.client.mvicoreexample

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.onNodeWithTag
import com.bumble.appyx.v2.sandbox.client.mvicoreexample.MviCoreExampleView.Companion.InitialStateButtonTextTag
import com.bumble.appyx.v2.sandbox.client.mvicoreexample.MviCoreExampleView.Companion.InitialStateTextTag
import com.bumble.appyx.v2.sandbox.client.mvicoreexample.MviCoreExampleView.Companion.LoadingTestTag

internal class MviCorePageObject(compose: SemanticsNodeInteractionsProvider) {

    val loader = compose.onNodeWithTag(LoadingTestTag)
    val initialStateText = compose.onNodeWithTag(InitialStateTextTag)
    val initialStateButtonTextTag = compose.onNodeWithTag(InitialStateButtonTextTag)
}
