package com.github.zsoltk.composeribs.core.integrationpoint

import android.os.Bundle
import com.github.zsoltk.composeribs.core.integrationpoint.activitystarter.ActivityStarter
import com.github.zsoltk.composeribs.core.integrationpoint.permissionrequester.PermissionRequester
import com.github.zsoltk.composeribs.core.integrationpoint.requestcode.RequestCodeRegistry
import com.github.zsoltk.composeribs.core.node.Node

abstract class IntegrationPoint(protected val savedInstanceState: Bundle?) {

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

}
