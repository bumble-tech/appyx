package com.bumble.appyx.app.node.onboarding.screen.app

import android.os.Parcelable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import com.bumble.appyx.app.composable.Page
import com.bumble.appyx.app.node.child.GenericChildNode
import com.bumble.appyx.app.ui.AppyxSampleAppTheme
import com.bumble.appyx.core.integration.NodeHost
import com.bumble.appyx.core.integrationpoint.IntegrationPointStub
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.modality.BuildContext.Companion.root
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.navigation.model.permanent.PermanentNavModel
import kotlinx.coroutines.delay
import kotlinx.parcelize.Parcelize

@ExperimentalUnitApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
class StatefulNode1(
    buildContext: BuildContext
) : ParentNode<StatefulNode1.NavTarget>(
    buildContext = buildContext,
    navModel = PermanentNavModel(
        savedStateMap = buildContext.savedStateMap
    )
) {
    sealed class NavTarget : Parcelable {
        @Parcelize
        data class Child(val counterStartValue: Int) : NavTarget()
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
        when (navTarget) {
            is NavTarget.Child -> GenericChildNode(buildContext, navTarget.counterStartValue)
        }

    @Composable
    override fun View(modifier: Modifier) {
        Page(
            modifier = modifier,
            title = "Stateful",
            body = "Each Node on this screen has some state:" +
                "\n\n1. The counter represents data from a background process (e.g. server)." +
                "\n2. You can also tap them to change their colour. Try it!"
        ) {
            Column(Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .weight(0.5f)
                        .padding(bottom = 8.dp)
                ) {
                    ChildInABox(
                        navTarget = NavTarget.Child(BASE_COUNTER),
                        showWithDelay = BASE_DELAY,
                        modifier = Modifier
                            .weight(0.5f)
                            .padding(end = 8.dp)
                    )
                    ChildInABox(
                        navTarget = NavTarget.Child(BASE_COUNTER * 2),
                        showWithDelay = BASE_DELAY * 2,
                        modifier = Modifier
                            .weight(0.5f)
                    )
                }
                Row(
                    modifier = Modifier
                        .weight(0.5f)
                ) {
                    ChildInABox(
                        navTarget = NavTarget.Child(BASE_COUNTER * 3),
                        showWithDelay = BASE_DELAY * 3,
                        modifier = Modifier
                            .weight(0.5f)
                            .padding(end = 8.dp)
                    )
                    ChildInABox(
                        navTarget = NavTarget.Child(BASE_COUNTER * 4),
                        showWithDelay = BASE_DELAY * 4,
                        modifier = Modifier
                            .weight(0.5f)
                    )
                }
            }
        }
    }

    @Composable
    private fun ChildInABox(navTarget: NavTarget, showWithDelay: Long, modifier: Modifier = Modifier) {
        PermanentChild(navTarget) { child ->
            Box(modifier) {
                var visible by remember { mutableStateOf(false) }
                AnimatedVisibility(
                    visible = visible,
                    enter = scaleIn()
                ) {
                    child()
                }

                LaunchedEffect(Unit) {
                    delay(showWithDelay)
                    visible = true
                }
            }
        }
    }

    companion object {
        private const val BASE_COUNTER = 100
        private const val BASE_DELAY = 200L
    }
}

@Preview
@Composable
@ExperimentalUnitApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
fun StatefulNode1Preview() {
    AppyxSampleAppTheme(darkTheme = false) {
        PreviewContent()
    }
}

@Preview
@Composable
@ExperimentalUnitApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
fun StatefulNode1PreviewDark() {
    AppyxSampleAppTheme(darkTheme = true) {
        PreviewContent()
    }
}

@Composable
@ExperimentalUnitApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
private fun PreviewContent() {
    Surface(color = MaterialTheme.colors.background) {
        Box(Modifier.fillMaxSize()) {
            NodeHost(integrationPoint = IntegrationPointStub()) {
                StatefulNode1(
                    root(null)
                )
            }
        }
    }
}
