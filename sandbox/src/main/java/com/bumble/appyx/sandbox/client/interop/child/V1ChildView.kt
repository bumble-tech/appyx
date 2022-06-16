package com.bumble.appyx.sandbox.client.interop.child

import android.view.ViewGroup
import com.badoo.ribs.core.customisation.inflate
import com.badoo.ribs.core.view.AndroidRibView
import com.badoo.ribs.core.view.RibView
import com.badoo.ribs.core.view.ViewFactory
import com.badoo.ribs.core.view.ViewFactoryBuilder
import com.bumble.appyx.R.layout

interface V1ChildView : RibView {

    interface Factory : ViewFactoryBuilder<Nothing?, V1ChildView>
}

class V1ChildViewImpl private constructor(
    override val androidView: ViewGroup
) : AndroidRibView(), V1ChildView {

    class Factory : V1ChildView.Factory {
        override fun invoke(deps: Nothing?): ViewFactory<V1ChildView> =
            ViewFactory {
                V1ChildViewImpl(androidView = it.inflate(layout.rib_child))
            }
    }
}
