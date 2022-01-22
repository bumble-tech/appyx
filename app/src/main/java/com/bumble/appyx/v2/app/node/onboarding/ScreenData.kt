package com.bumble.appyx.v2.app.node.onboarding

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ScreenData(
    val title: String,
    val body: String,
) : Parcelable

internal val onboardingScreenWelcome = ScreenData(
    title = "Hi there!",
    body = "Appyx is an Android application framework built with love on top of Jetpack Compose."
)

internal val onboardingScreenNodes = ScreenData(
    title = "Nodes",
    body = "The app is organised into a tree hierarchy of Nodes." +
        "\n\nNodes have @Composable UI, each have their own lifecycle on and off the screen, and can choose which of their children to delegate the control flow to."
)

internal val onboardingScreenLifecycle1 = ScreenData(
    title = "Stateful",
    body = "Nodes can persist data whether part of the UI or not. " +
        "\n\nEach child Node on this screen has some state:" +
        "\n1. The counter represents data from a background process (e.g. server)." +
        "\n2. You can also long press them to change their colour." +
        "\n\nTry it!"
)

internal val onboardingScreenLifecycle2 = ScreenData(
    title = "Off the screen?",
    body = "Nodes are alive even when they're not visible. " +
        "\n\nTry returning to the previous screen!" +
        "\n\nYou should see that the counters kept on working in the background, and changes you made to colours are persisted."
)
