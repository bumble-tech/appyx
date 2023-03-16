package com.bumble.appyx.navigation.node.teaser.promoter

import android.os.Parcelable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.lifecycle.coroutineScope
import com.bumble.appyx.navigation.node.child.GenericChildNode
import com.bumble.appyx.navigation.node.teaser.promoter.PromoterTeaserNode.NavTarget
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.navmodel.promoter.navmodel.Promoter
import com.bumble.appyx.navmodel.promoter.navmodel.operation.addFirst
import com.bumble.appyx.navmodel.promoter.navmodel.operation.promoteAll
import com.bumble.appyx.navmodel.promoter.transitionhandler.rememberPromoterTransitionHandler
import kotlinx.coroutines.delay
import kotlinx.parcelize.Parcelize
import kotlin.random.Random

@ExperimentalUnitApi
class PromoterTeaserNode(
    buildContext: BuildContext,
    private val promoter: Promoter<NavTarget> = Promoter(),
) : ParentNode<NavTarget>(
    buildContext = buildContext,
    navModel = promoter
) {

    init {
        lifecycle.coroutineScope.launchWhenStarted {
            repeat(4) {
                promoter.addFirst(NavTarget.Child((it + 1) * 100))
                promoter.promoteAll()
            }
            delay(500)
            repeat(4) {
                delay(1500)
                promoter.addFirst(NavTarget.Child((it + 5) * 100))
                promoter.promoteAll()
            }
            finish()
        }
    }

    sealed class NavTarget : Parcelable {
        @Parcelize
        data class Child(val int: Int = Random.nextInt(1000)) : NavTarget()
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
        when (navTarget) {
            is NavTarget.Child -> GenericChildNode(buildContext, navTarget.int)
        }

    @Composable
    override fun View(modifier: Modifier) {
        val childSize = remember { 100.dp }
        Children(
            modifier = Modifier.fillMaxSize(),
            navModel = promoter,
            transitionHandler = rememberPromoterTransitionHandler(childSize) {
                spring(stiffness = Spring.StiffnessVeryLow / 4)
            }
        ) {
            children<NavTarget> { child ->
                child(Modifier.size(childSize))
            }
        }
    }
}

