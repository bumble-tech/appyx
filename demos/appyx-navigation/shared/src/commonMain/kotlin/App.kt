import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.ui.parallax.BackStackParallax
import com.bumble.appyx.components.backstack.ui.slider.BackStackSlider
import com.bumble.appyx.navigation.integration.NodeHost
import com.bumble.appyx.navigation.integration.ScreenSize
import com.bumble.appyx.navigation.integrationpoint.IntegrationPoint
import com.bumble.appyx.navigation.node.container.ContainerNode
import com.bumble.appyx.navigation.platform.PlatformLifecycleRegistry
import com.bumble.appyx.navigation.ui.AppyxSampleAppTheme
import com.bumble.appyx.utils.customisations.MutableNodeCustomisationDirectory
import com.bumble.appyx.utils.customisations.NodeCustomisation
import com.bumble.appyx.utils.customisations.NodeCustomisationDirectory
import com.bumble.appyx.utils.customisations.NodeCustomisationDirectoryImpl
import kotlin.reflect.KClass

@Composable
internal fun App() {
    AppyxSampleAppTheme {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ) {
            val platformLifecycleRegistry = remember {
                object : PlatformLifecycleRegistry() {}
            }

            val dpWidth = maxWidth
            val dpHeight = maxHeight
            NodeHost(
                lifecycle = platformLifecycleRegistry,
                integrationPoint = remember { object : IntegrationPoint() {
                    override val isChangingConfigurations: Boolean
                        get() = false

                    override fun onRootFinished() {}

                    override fun handleUpNavigation() {}
                } },
                modifier = Modifier,
                screenSize = ScreenSize(
                    widthDp = dpWidth,
                    heightDp = dpHeight,
                ),
            ) {
                ContainerNode(
                    buildContext = it,
                    backStack = BackStack(
                        model = BackStackModel(
                            initialTargets = listOf(ContainerNode.InteractionTarget.Selector),
                            savedStateMap = it.savedStateMap,
                        ),
                        motionController = { BackStackParallax(it) },
                        gestureFactory = { BackStackParallax.Gestures(it) }
                    )
                )
            }
        }
    }
}