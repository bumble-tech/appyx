package com.bumble.appyx.v2.app.node.onboarding

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class ScreenData : Parcelable {
    abstract val title: String
    abstract val body: String

    @Parcelize
    data class PlainWithImage(
        override val title: String,
        override val body: String,
        val imageRes: Int
    ) : ScreenData()

    @Parcelize
    data class TreeIllustration(
        override val title: String,
        override val body: String,
    ) : ScreenData()

    @Parcelize
    data class StatefulNodeIllustration(
        override val title: String,
        override val body: String,
    ) : ScreenData()
}

internal val onboardingScreenWelcome = ScreenData.PlainWithImage(
    imageRes = 0,
    title = "Hi there!",
    body = "Appyx is an Android application framework built with love on top of Jetpack Compose."
)

internal val onboardingScreenNodes = ScreenData.TreeIllustration(
    title = "Nodes",
    body = "The app is organised into a tree hierarchy of Nodes." +
        "\n\nNodes have @Composable UI, each have their own lifecycle on and off the screen, and can choose which of their children to delegate the control flow to."
)

internal val onboardingScreenLifecycle1 = ScreenData.StatefulNodeIllustration(
    title = "Stateful",
    body = "Each Node on this screen has some state:" +
        "\n\n1. The counter represents data from a background process (e.g. server  )." +
        "\n2. You can also long press them to change their colour. Try it!"
)

internal val onboardingScreenLifecycle2 = ScreenData.PlainWithImage(
    imageRes = 0,
    title = "Off the screen?",
    body = "Nodes are alive even when they're not visible. " +
        "\n\nTry returning to the previous screen!" +
        "\n\nYou should see that the counters kept on working in the background, and changes you made to colours are persisted."
)
