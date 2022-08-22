package com.bumble.appyx.sandbox.client.mvicoreexample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.bumble.appyx.connectable.rx2.Connectable
import com.bumble.appyx.connectable.rx2.NodeConnector
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreChildNode1.Input
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreChildNode1.Output
import kotlin.random.Random.Default.nextInt

class MviCoreChildNode1(
    buildContext: BuildContext,
    private val connector: NodeConnector<Input, Output> = NodeConnector()
) : Node(buildContext),
    Connectable<Input, Output> by connector {

    sealed class Input {
        object ExampleInput1: Input()
    }

    sealed class Output {
        data class Result(val data: String) : Output()
    }

    @Composable
    override fun View(modifier: Modifier) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(color = Color.LightGray)
        ) {
            Button(
                onClick = { connector.output.accept(Output.Result("Child1 output ${nextInt()}")) },
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text(text = "Generate and send output")
            }
        }
    }
}
