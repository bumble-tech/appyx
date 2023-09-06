import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.ui.parallax.BackStackParallax
import com.bumble.appyx.navigation.navigation.IOSNodeHost
import com.bumble.appyx.navigation.node.container.ContainerNode
import com.bumble.appyx.navigation.ui.AppyxSampleAppTheme

fun MainViewController() = ComposeUIViewController {
   AppyxSampleAppTheme {
       IOSNodeHost(
           modifier = Modifier
       ) { buildContext ->
           ContainerNode(
               buildContext = buildContext,
               backStack = BackStack(
                   model = BackStackModel(
                       initialTargets = listOf(ContainerNode.InteractionTarget.Selector),
                       savedStateMap = buildContext.savedStateMap,
                   ),
                   motionController = { BackStackParallax(it) },
                   gestureFactory = { BackStackParallax.Gestures(it) }
               )
           )
       }
   }
}