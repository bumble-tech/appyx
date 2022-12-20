package com.bumble.appyx.sandbox.client.mvicoreexample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.bumble.appyx.interop.rx2.connectable.Connectable
import com.bumble.appyx.interop.rx2.connectable.NodeConnector
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreChildNode2.Input
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreChildNode2.Output
import kotlin.random.Random

class MviCoreChildNode2(
    buildContext: BuildContext,
    private val connector: NodeConnector<Input, Output> = NodeConnector()
) : Node(buildContext), Connectable<Input, Output> by connector {

    sealed class Input

    sealed class Output {
        data class Result(val data: String) : Output()
    }

    @Composable
    override fun View(modifier: Modifier) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(color = Color.Black)
        ) {
            Button(
                onClick = { connector.output.accept(Output.Result("Child2 output ${Random.nextInt()}")) },
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text(text = "Generate and send output")
            }
        }
    }
}
