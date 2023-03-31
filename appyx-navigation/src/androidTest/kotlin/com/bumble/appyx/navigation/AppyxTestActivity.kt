package com.bumble.appyx.navigation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.bumble.appyx.navigation.integrationpoint.NodeActivity

/**
 * TODO: Move to testing module for appyx 2.0
 */
open class AppyxTestActivity : NodeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val composableView = composableView
        setContent {
            requireNotNull(composableView) { "AppyxTestActivity View has not been setup" }
            composableView(this)
        }
    }

    companion object {
        var composableView: (@Composable (activity: AppyxTestActivity) -> Unit)? = null
    }

}
