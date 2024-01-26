package com.bumble.appyx.utils.interop.ribs

import android.os.Bundle
import android.view.ViewGroup
import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.modality.BuildContext

class AppyxRibsInteropActivity : InteropActivity() {

    lateinit var ribsNode: RibsNode

    override val rootViewGroup: ViewGroup
        get() = findViewById(android.R.id.content)

    override fun createRib(savedInstanceState: Bundle?): Rib =
        RibsNodeBuilder()
            .build(BuildContext.root(savedInstanceState), appyxIntegrationPoint)
            .also { ribsNode = it }

}
