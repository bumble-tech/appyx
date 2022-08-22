package com.bumble.appyx.sandbox.client.mvicoreexample.feature

import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreChildNode2
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreExampleNode.Input

internal object InputToInputChild2 : (Input) -> MviCoreChildNode2.Input {
    override fun invoke(output: Input): MviCoreChildNode2.Input =
        when (output) {
            is Input.ExampleInput -> MviCoreChildNode2.Input.ExampleInput
        }
}
