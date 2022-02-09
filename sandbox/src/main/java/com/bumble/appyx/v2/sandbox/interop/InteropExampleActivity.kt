package com.bumble.appyx.v2.sandbox.interop

import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import com.badoo.ribs.android.RibActivity
import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.modality.BuildContext
import com.bumble.appyx.v2.sandbox.interop.NodeV1Builder.RootParams

class InteropExampleActivity : RibActivity() {

    private lateinit var rootView: FrameLayout
    override val rootViewGroup: ViewGroup get() = rootView

    override fun createRib(savedInstanceState: Bundle?): Rib {
        return NodeV1Builder().build(buildContext = BuildContext.root(savedInstanceState), payload = RootParams())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        rootView = FrameLayout(this)
        setContentView(rootView)
        super.onCreate(savedInstanceState)
    }
}
