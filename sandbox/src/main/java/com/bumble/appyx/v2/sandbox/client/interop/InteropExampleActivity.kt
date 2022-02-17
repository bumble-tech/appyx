package com.bumble.appyx.v2.sandbox.client.interop

import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.modality.BuildContext
import com.bumble.appyx.interop.v1v2.RibInteropActivity
import com.bumble.appyx.v2.sandbox.client.interop.parent.V1ParentBuilder

class InteropExampleActivity : RibInteropActivity() {

    private lateinit var rootView: FrameLayout
    override val rootViewGroup: ViewGroup get() = rootView

    override fun createRib(savedInstanceState: Bundle?): Rib {
        return V1ParentBuilder().build(buildContext = BuildContext.root(savedInstanceState))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        rootView = FrameLayout(this)
        setContentView(rootView)
        super.onCreate(savedInstanceState)
    }
}
