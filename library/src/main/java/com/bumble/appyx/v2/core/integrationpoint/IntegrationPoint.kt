package com.bumble.appyx.v2.core.integrationpoint

import android.os.Bundle
import androidx.compose.runtime.Stable
import com.bumble.appyx.v2.core.integrationpoint.activitystarter.ActivityStarter
import com.bumble.appyx.v2.core.integrationpoint.permissionrequester.PermissionRequester
import com.bumble.appyx.v2.core.integrationpoint.requestcode.RequestCodeRegistry
import com.bumble.appyx.v2.core.node.Node
import com.bumble.appyx.v2.core.routing.upnavigation.UpNavigationHandler

@Stable
abstract class IntegrationPoint(
    protected val savedInstanceState: Bundle?
) : UpNavigationHandler {

    protected val requestCodeRegistry = RequestCodeRegistry(savedInstanceState)

    abstract val activityStarter: ActivityStarter

    abstract val permissionRequester: PermissionRequester

    private var _root: Node? = null
    private val root: Node
        get() = _root ?: error("Root has not been initialised. Did you forget to call attach?")

    fun attach(root: Node) {
        if (_root != null) error("A root has already been attached to this integration point")
        if (!root.isRoot) error("Trying to attach non-root Node")
        this._root = root
        root.integrationPoint = this
    }

    fun onSaveInstanceState(outState: Bundle) {
        requestCodeRegistry.onSaveInstanceState(outState)
    }

    abstract fun onRootFinished()
}
