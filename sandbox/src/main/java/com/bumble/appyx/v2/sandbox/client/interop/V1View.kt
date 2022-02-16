package com.bumble.appyx.v2.sandbox.client.interop

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.badoo.ribs.core.Node
import com.badoo.ribs.core.customisation.inflate
import com.badoo.ribs.core.view.AndroidRibView
import com.badoo.ribs.core.view.RibView
import com.badoo.ribs.core.view.ViewFactory
import com.badoo.ribs.core.view.ViewFactoryBuilder
import com.bumble.appyx.v2.R

interface V1View : RibView {

    interface Factory : ViewFactoryBuilder<Nothing?, V1View>
}

class V1ViewImpl private constructor(
    override val androidView: ViewGroup,
) : AndroidRibView(), V1View {

    private val container = androidView.findViewById<ViewGroup>(R.id.child)

    class Factory(@LayoutRes private val layoutRes: Int = R.layout.rib_root) : V1View.Factory {
        override fun invoke(deps: Nothing?): ViewFactory<V1View> =
            ViewFactory {
                V1ViewImpl(
                    androidView = it.inflate(layoutRes),
                )
            }
    }

    override fun getParentViewForSubtree(subtreeOf: Node<*>): ViewGroup = container

}
