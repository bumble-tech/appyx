package com.bumble.appyx.testing.ui.rules

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.Modifier
import com.bumble.appyx.v2.core.node.NodeView

class AppyxViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            view?.View(modifier = Modifier)
        }
    }

    companion object {
        var view: NodeView? = null
    }

}
