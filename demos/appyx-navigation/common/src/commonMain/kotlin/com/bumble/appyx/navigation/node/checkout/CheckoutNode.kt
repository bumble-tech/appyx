package com.bumble.appyx.navigation.node.checkout

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.operation.newRoot
import com.bumble.appyx.components.backstack.operation.push
import com.bumble.appyx.components.backstack.ui.parallax.BackStackParallax
import com.bumble.appyx.components.backstack.ui.slider.BackStackSlider
import com.bumble.appyx.interactions.core.ui.gesture.GestureFactory
import com.bumble.appyx.navigation.composable.AppyxNavigationContainer
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.cart.Cart
import com.bumble.appyx.navigation.node.checkout.CheckoutNode.NavTarget
import com.bumble.appyx.navigation.platform.IOS_PLATFORM_NAME
import com.bumble.appyx.navigation.platform.getPlatformName
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize

class CheckoutNode(
    buildContext: BuildContext,
    private val cart: Cart,
    private val backStack: BackStack<NavTarget> = BackStack(
        model = BackStackModel(
            initialTargets = listOf(NavTarget.CartItems),
            savedStateMap = buildContext.savedStateMap,
        ),
        visualisation = {
            if (getPlatformName() == IOS_PLATFORM_NAME) {
                BackStackParallax(it)
            } else {
                BackStackSlider(it)
            }
        },
        gestureFactory = {
            if (getPlatformName() == IOS_PLATFORM_NAME) {
                BackStackParallax.Gestures(it)
            } else {
                GestureFactory.Noop()
            }
        }
    )
) : ParentNode<NavTarget>(
    buildContext = buildContext,
    appyxComponent = backStack
) {
    sealed class NavTarget : Parcelable {
        @Parcelize
        object CartItems : NavTarget()

        @Parcelize
        object Address : NavTarget()

        @Parcelize
        object Shipping : NavTarget()

        @Parcelize
        object Payment : NavTarget()

        @Parcelize
        object Success : NavTarget()
    }

    override fun buildChildNode(navTarget: NavTarget, buildContext: BuildContext): Node =
        when (navTarget) {
            is NavTarget.CartItems -> CartItemsNode(buildContext, cart) {
                backStack.push(NavTarget.Address)
            }

            is NavTarget.Address -> AddressNode(buildContext) {
                backStack.push(NavTarget.Shipping)
            }

            is NavTarget.Shipping -> ShippingDetailsNode(buildContext) {
                backStack.push(NavTarget.Payment)
            }

            is NavTarget.Payment -> PaymentNode(buildContext) {
                cart.clear()
                backStack.newRoot(NavTarget.Success)
            }

            is NavTarget.Success -> OrderConfirmedNode(buildContext) {
                backStack.newRoot(NavTarget.CartItems)
            }
        }

    @Composable
    override fun View(modifier: Modifier) {
        AppyxNavigationContainer(
            appyxComponent = backStack,
            modifier = Modifier
        )
    }
}
