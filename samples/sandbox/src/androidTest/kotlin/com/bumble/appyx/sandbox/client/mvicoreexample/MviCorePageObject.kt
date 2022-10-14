package com.bumble.appyx.sandbox.client.mvicoreexample

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.onNodeWithTag
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreExampleViewImpl.Companion.InitialStateButtonTextTag
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreExampleViewImpl.Companion.InitialStateTextTag
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreExampleViewImpl.Companion.LoadingTestTag
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreExampleViewImpl.Companion.TitleTag

internal class MviCorePageObject(compose: SemanticsNodeInteractionsProvider) {

    val titleText = compose.onNodeWithTag(TitleTag)
    val loader = compose.onNodeWithTag(LoadingTestTag)
    val initialStateText = compose.onNodeWithTag(InitialStateTextTag)
    val initialStateButtonText = compose.onNodeWithTag(InitialStateButtonTextTag)
}
