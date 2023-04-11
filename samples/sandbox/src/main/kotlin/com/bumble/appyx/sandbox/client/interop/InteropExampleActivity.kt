package com.bumble.appyx.sandbox.client.interop

import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.customisation.RibCustomisationDirectory
import com.badoo.ribs.core.customisation.RibCustomisationDirectoryImpl
import com.badoo.ribs.core.modality.BuildContext
import com.bumble.appyx.interop.ribs.InteropActivity
import com.bumble.appyx.sandbox.client.container.ContainerNode
import com.bumble.appyx.sandbox.client.interop.parent.RibsParentBuilder
import com.bumble.appyx.utils.customisations.put

class InteropExampleActivity : InteropActivity() {

    private lateinit var rootView: FrameLayout
    override val rootViewGroup: ViewGroup get() = rootView

    override fun createRib(savedInstanceState: Bundle?): Rib {
        return RibsParentBuilder(appyxIntegrationPoint).build(
            buildContext = BuildContext.root(
                savedInstanceState= savedInstanceState,
                customisations = ribCustomisations()
            )
        )
    }

    private fun ribCustomisations(): RibCustomisationDirectory =
        RibCustomisationDirectoryImpl().apply {
            put {
                ContainerNode.Customisation(name = "Interop ContainerNode customisation")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        rootView = FrameLayout(this)
        setContentView(rootView)
        super.onCreate(savedInstanceState)
    }
}
