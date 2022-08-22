package com.bumble.appyx.sandbox.client.mvicoreexample.feature

import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreChildNode1
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreExampleNode.Input

internal object InputToInputChild1 : (Input) -> MviCoreChildNode1.Input {
    override fun invoke(output: Input): MviCoreChildNode1.Input =
        when (output) {
            is Input.ExampleInput -> MviCoreChildNode1.Input.ExampleInput
        }
}
