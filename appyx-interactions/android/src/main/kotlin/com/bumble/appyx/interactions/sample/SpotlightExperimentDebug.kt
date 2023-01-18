package com.bumble.appyx.interactions.sample

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable


@ExperimentalMaterialApi
@Composable
fun SpotlightExperimentDebug() {
    // FIXME

//    val spotlight = remember {
//        SpotlightModel(
//            items = listOf(Child1, Child2, Child3, Child4, Child5, Child6, Child7),
//        )
//    }
//    val coroutineScope = rememberCoroutineScope()
//    val inputSource = remember { DebuglProgressInputSource(spotlight, coroutineScope) }
//
//    LaunchedEffect(Unit) {
//        inputSource.next()
//        inputSource.next()
//        inputSource.next()
//        inputSource.previous()
//        inputSource.last()
//        inputSource.first()
//    }
//
//    var elementSize by remember { mutableStateOf(IntSize(0, 0)) }
//    val transitionParams by createTransitionParams(elementSize)
//    val uiProps = remember(transitionParams) {
//        SpotlightSlider<NavTarget>(
//            transitionParams = transitionParams,
//            orientation = Orientation.Horizontal
//        )
//    }
//    val render = remember(uiProps) { spotlight.segments.map { uiProps.map(it) } }
//
//    Column(
//        Modifier
//            .fillMaxWidth()
//            .background(appyx_dark)
//    ) {
//        KnobControl(onValueChange = {
//            inputSource.setNormalisedProgress(it)
//        })
//
//        Children(
//            modifier = Modifier.padding(
//                horizontal = 64.dp,
//                vertical = 12.dp
//            ),
//            frameModel = render.collectAsState(listOf()),
//            onElementSizeChanged = { elementSize = it },
//            element = {
//                Element(
//                    frameModel = it,
//                    modifier = Modifier
//                        .fillMaxSize()
//                )
//            }
//        )
//    }
}
