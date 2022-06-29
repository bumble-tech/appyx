package com.bumble.appyx.sandbox.client.interop.child

import android.view.ViewGroup
import com.badoo.ribs.core.customisation.inflate
import com.badoo.ribs.core.view.AndroidRibView
import com.badoo.ribs.core.view.RibView
import com.badoo.ribs.core.view.ViewFactory
import com.badoo.ribs.core.view.ViewFactoryBuilder
import com.bumble.appyx.R.layout

interface RibsChildView : RibView {

    interface Factory : ViewFactoryBuilder<Nothing?, RibsChildView>
}

class RibsChildViewImpl private constructor(
    override val androidView: ViewGroup
) : AndroidRibView(), RibsChildView {

    class Factory : RibsChildView.Factory {
        override fun invoke(deps: Nothing?): ViewFactory<RibsChildView> =
            ViewFactory {
                RibsChildViewImpl(androidView = it.inflate(layout.rib_child))
            }
    }
}
