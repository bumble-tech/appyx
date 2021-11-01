package com.github.zsoltk.composeribs

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.github.zsoltk.composeribs.client.container.ContainerNode
import com.github.zsoltk.composeribs.client.combined.CombinedRoutingSourceNode
import com.github.zsoltk.composeribs.core.integration.NodeHost
import com.github.zsoltk.composeribs.core.modality.BuildContext
import com.github.zsoltk.composeribs.core.routing.FallbackUpNavigationHandler
import com.github.zsoltk.composeribs.ui.Rf1Theme

class MainActivity : AppCompatActivity() {

    private val upNavigationHandler = FallbackUpNavigationHandler { onBackPressed() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Rf1Theme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Column {
                        NodeHost(
                            upNavigationHandler = upNavigationHandler
                        ) {
                            CombinedRoutingSourceNode(buildContext = it)
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Rf1Theme {
        Column {
            ContainerNode(buildContext = BuildContext.root(null)).Compose()
        }
    }
}
