package com.bumble.appyx.sandbox.client.mvicoreexample.feature

import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreChildNode2.Output
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.MviCoreExampleFeature.Wish

internal object OutputChild2ToWish : (Output) -> Wish {

    override fun invoke(output: Output): Wish =
        when (output) {
            is Output.Result -> Wish.ChildInput(output.data)
        }
}
