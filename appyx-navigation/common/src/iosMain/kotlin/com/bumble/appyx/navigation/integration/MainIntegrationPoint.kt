package com.bumble.appyx.navigation.integration

import platform.UIKit.UIViewController
import platform.UIKit.navigationController

class MainIntegrationPoint: IntegrationPoint() {
    private lateinit var viewController: UIViewController

    override val isChangingConfigurations: Boolean
        get() = false

    fun setViewController(viewController: UIViewController) {
        this.viewController = viewController
    }

    override fun onRootFinished() {
        viewController.dismissModalViewControllerAnimated(false)
    }

    override fun handleUpNavigation() {
        viewController.navigationController?.popViewControllerAnimated(false)
    }
}
