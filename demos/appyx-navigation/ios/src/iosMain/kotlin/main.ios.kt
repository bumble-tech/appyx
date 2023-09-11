import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.ComposeUIViewController
import androidx.compose.ui.zIndex
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.operation.pop
import com.bumble.appyx.components.backstack.ui.parallax.BackStackParallax
import com.bumble.appyx.components.backstack.ui.slider.BackStackSlider
import com.bumble.appyx.navigation.integration.IOSNodeHost
import com.bumble.appyx.navigation.node.container.ContainerNode
import com.bumble.appyx.navigation.ui.AppyxSampleAppTheme

fun MainViewController() = ComposeUIViewController {
   AppyxSampleAppTheme {

       val backStackModel = remember {
           BackStackModel<ContainerNode.InteractionTarget>(
               initialTargets = listOf(ContainerNode.InteractionTarget.Selector),
               savedStateMap = null,
           )
       }
       val coroutineScope = rememberCoroutineScope()
       val backStack: BackStack<ContainerNode.InteractionTarget> = BackStack(
           scope = coroutineScope,
           model = backStackModel,
           motionController = { BackStackParallax(it) },
       )
       val backAction = { backStack.pop() }

       Box(modifier = Modifier.fillMaxSize()) {
           BackButton(action = backAction)

           IOSNodeHost(
               modifier = Modifier
           ) { buildContext ->
               ContainerNode(
                   buildContext = buildContext,
                   backStack = backStack,
               )
           }
       }
   }
}

@Composable
private fun BackButton(action: () -> Unit) {
    IconButton(
        onClick =  { action.invoke() },
        modifier = Modifier.zIndex(5f)
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            tint = Color.White,
            contentDescription = ""
        )
    }
}