package com.github.zsoltk.composeribs

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.github.zsoltk.composeribs.client.container.Container
import com.github.zsoltk.composeribs.client.container.ContainerBuilder
import com.github.zsoltk.composeribs.ui.Rf1Theme
import com.github.zsoltk.composeribs.Random

()

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Rf1Theme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Random()

//                    Column {
//                        Greeting("Android")
//                        ContainerBuilder(object : Container.Dependency {})
//                            .build()
//                            .Compose(withView = true)
//                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Rf1Theme {
        Column {
//            Greeting("Android")
            ContainerBuilder(object : Container.Dependency {})
                .build()
                .Compose(withView = true)
        }
    }
}
