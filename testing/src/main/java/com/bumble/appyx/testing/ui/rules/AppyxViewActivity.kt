package com.bumble.appyx.testing.ui.rules

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable

class AppyxViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val composableView = composableView
        setContent {
            requireNotNull(composableView) { "AppyxViewActivity View has not been setup" }
            composableView()
        }
    }

    companion object {
        var composableView: (@Composable () -> Unit)? = null
    }

}
