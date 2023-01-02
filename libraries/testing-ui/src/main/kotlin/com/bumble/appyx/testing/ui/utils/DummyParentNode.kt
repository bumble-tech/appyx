package com.bumble.appyx.testing.ui.utils

import android.os.Parcelable
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.navigation.EmptyState
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.node
import com.bumble.appyx.testing.ui.utils.DummyParentNode.DummyNavTarget
import kotlinx.parcelize.Parcelize

class DummyParentNode : ParentNode<DummyNavTarget>(
    navModel = DummyNavModel<DummyNavTarget, EmptyState>(),
    buildContext = BuildContext.root(savedStateMap = null)
) {
    @Suppress("PARCELABLE_PRIMARY_CONSTRUCTOR_IS_EMPTY")
    @Parcelize
    class DummyNavTarget internal constructor() : Parcelable

    override fun resolve(navTarget: DummyNavTarget, buildContext: BuildContext) = node(buildContext) { }
}
