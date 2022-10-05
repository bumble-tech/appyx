package com.bumble.appyx.sample.navigtion.compose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.CompositionLocalProvider
import com.bumble.appyx.core.integrationpoint.LocalIntegrationPoint
import com.bumble.appyx.core.integrationpoint.NodeActivity

class ComposeNavigationActivity : NodeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 'integrationPoint' must be provided to ensure it can be accessed from within the
            // Jetpack compose navigation graph.
            CompositionLocalProvider(
                LocalIntegrationPoint provides appyxIntegrationPoint,
            ) {
                Surface(color = MaterialTheme.colors.background) {
                    ComposeNavigationRoot()
                }
            }
        }
    }
}


