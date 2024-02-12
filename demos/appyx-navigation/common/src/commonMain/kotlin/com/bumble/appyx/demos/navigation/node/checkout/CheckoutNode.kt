package com.bumble.appyx.demos.navigation.node.checkout

import com.bumble.appyx.components.backstack.node.backStackNode
import com.bumble.appyx.components.backstack.operation.newRoot
import com.bumble.appyx.components.backstack.operation.push
import com.bumble.appyx.components.backstack.ui.parallax.BackStackParallax
import com.bumble.appyx.components.backstack.ui.slider.BackStackSlider
import com.bumble.appyx.demos.navigation.node.cart.Cart
import com.bumble.appyx.demos.navigation.node.checkout.NavTarget.Address
import com.bumble.appyx.demos.navigation.node.checkout.NavTarget.CartItems
import com.bumble.appyx.demos.navigation.node.checkout.NavTarget.Payment
import com.bumble.appyx.demos.navigation.node.checkout.NavTarget.Shipping
import com.bumble.appyx.demos.navigation.node.checkout.NavTarget.Success
import com.bumble.appyx.interactions.gesture.GestureFactory
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.demos.navigation.platform.IOS_PLATFORM_NAME as IOS
import com.bumble.appyx.demos.navigation.platform.getPlatformName as platform

private enum class NavTarget {
    CartItems,
    Address,
    Shipping,
    Payment,
    Success
}

fun checkoutNode(nodeContext: NodeContext, cart: Cart): Node<*> =
    backStackNode(
        nodeContext = nodeContext,
        initialTarget = CartItems,
        mappings = { backStack, navTarget, childContext ->
            with(backStack) {
                when (navTarget) {
                    CartItems -> CartItemsNode(childContext, cart) {
                        push(Address)
                    }
                    Address -> AddressNode(childContext) {
                        push(Shipping)
                    }
                    Shipping -> ShippingDetailsNode(childContext) {
                        push(Payment)
                    }
                    Payment -> PaymentNode(childContext) {
                        cart.clear()
                        newRoot(Success)
                    }
                    Success -> OrderConfirmedNode(childContext) {
                        newRoot(CartItems)
                    }
                }
            }
        },
        visualisation = {
            if (platform() == IOS) BackStackParallax(it) else BackStackSlider(it)
        },
        gestureFactory = {
            if (platform() == IOS) BackStackParallax.Gestures(it) else GestureFactory.Noop()
        }
    )

