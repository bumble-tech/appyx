package com.bumble.appyx.navigation.node.checkout

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.operation.newRoot
import com.bumble.appyx.components.backstack.operation.push
import com.bumble.appyx.components.backstack.ui.slider.BackStackSlider
import com.bumble.appyx.navigation.composable.AppyxComponent
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.cart.Cart
import com.bumble.appyx.navigation.node.cart.CartItemsNode
import com.bumble.appyx.navigation.node.checkout.CheckoutNode.NavTarget
import com.bumble.appyx.navigation.node.orderconfirmed.OrderConfirmedNode
import com.bumble.appyx.navigation.node.payment.PaymentNode
import com.bumble.appyx.navigation.node.shippingdetails.ShippingDetailsNode
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
        visualisation = { BackStackSlider(it) }
    )
) : ParentNode<NavTarget>(
    buildContext = buildContext,
    appyxComponent = backStack
) {
    sealed class NavTarget : Parcelable {
        @Parcelize
        object CartItems : NavTarget()

        @Parcelize
        object ShippingDetails : NavTarget()

        @Parcelize
        object PaymentDetails : NavTarget()

        @Parcelize
        object OrderConfirmed : NavTarget()
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
        when (navTarget) {
            is NavTarget.CartItems -> CartItemsNode(
                buildContext = buildContext,
                cart = cart,
                checkoutAction = {
                    backStack.push(
                        NavTarget.ShippingDetails
                    )
                }
            )

            is NavTarget.PaymentDetails -> PaymentNode(
                buildContext = buildContext,
                confirmAction = {
                    backStack.push(NavTarget.OrderConfirmed)
                }
            )

            is NavTarget.OrderConfirmed -> OrderConfirmedNode(
                buildContext = buildContext,
                postViewAction = {
                    cart.clear()
                    backStack.newRoot(NavTarget.CartItems)
                }
            )

            is NavTarget.ShippingDetails -> ShippingDetailsNode(
                buildContext = buildContext,
                confirmAction = {
                    backStack.push(NavTarget.PaymentDetails)
                }
            )
        }

    @Composable
    override fun View(modifier: Modifier) {
        AppyxComponent(
            appyxComponent = backStack,
            modifier = Modifier
        )
    }
}

