package com.bumble.appyx.testing.ui.rules

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.bumble.appyx.core.integrationpoint.NodeActivity

class AppyxViewActivity : NodeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val composableView = composableView
        setContent {
            requireNotNull(composableView) { "AppyxViewActivity View has not been setup" }
            composableView(this)
        }
    }

    companion object {
        var composableView: (@Composable (activity: AppyxViewActivity) -> Unit)? = null
    }

}
