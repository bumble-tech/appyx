package com.bumble.appyx.navigation.integration

class IntegrationPointStub : IntegrationPoint() {
    companion object {
        private const val ERROR = "You're accessing an IntegrationPointStub. " +
                "This means you're using a Node without ever integrating it to a proper IntegrationPoint. " +
                "This is fine during tests with limited scope, but it looks like the code that leads here " +
                "requires interfacing with a valid implementation. " +
                "You may be attempting to access the IntegrationPoint before it is attached to the Node."
    }

    override val isChangingConfigurations: Boolean
        get() = false

    override fun handleUpNavigation() {
        error(ERROR)
    }

    override fun onRootFinished() {
        error(ERROR)
    }
}
