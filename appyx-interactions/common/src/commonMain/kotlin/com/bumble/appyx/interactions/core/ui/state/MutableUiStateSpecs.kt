package com.bumble.appyx.interactions.core.ui.state

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
@Suppress("Unused")
annotation class MutableUiStateSpecs(
    val playMode: PlayMode = PlayMode.ConcurrentMode
) {
    enum class PlayMode {
        SequentialMode, ConcurrentMode
    }
}
