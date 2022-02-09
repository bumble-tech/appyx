package com.bumble.appyx.v2.sandbox.interop

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.badoo.ribs.core.Node
import com.badoo.ribs.core.customisation.inflate
import com.badoo.ribs.core.view.AndroidRibView
import com.badoo.ribs.core.view.RibView
import com.badoo.ribs.core.view.ViewFactory
import com.badoo.ribs.core.view.ViewFactoryBuilder
import com.bumble.appyx.v2.R

interface ViewV1 : RibView {

    interface Factory : ViewFactoryBuilder<Nothing?, ViewV1>
}

class ViewV1Impl private constructor(
    override val androidView: ViewGroup,
) : AndroidRibView(), ViewV1 {

    private val container = androidView.findViewById<ViewGroup>(R.id.child)

    class Factory(
        @LayoutRes private val layoutRes: Int = R.layout.rib_root
    ) : ViewV1.Factory {
        override fun invoke(deps: Nothing?): ViewFactory<ViewV1> =
            ViewFactory {
                ViewV1Impl(
                    androidView = it.inflate(layoutRes),
                )
            }
    }

    override fun getParentViewForSubtree(subtreeOf: Node<*>): ViewGroup = container

}
