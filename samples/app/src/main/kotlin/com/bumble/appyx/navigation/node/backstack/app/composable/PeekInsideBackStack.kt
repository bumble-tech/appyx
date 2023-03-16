package com.bumble.appyx.navigation.node.backstack.app.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumble.appyx.navigation.node.backstack.app.indexedbackstack.IndexedBackStack
import com.bumble.appyx.navigation.node.backstack.app.indexedbackstack.IndexedBackStack.State.Active
import com.bumble.appyx.navigation.node.backstack.app.indexedbackstack.IndexedBackStack.State.Created
import com.bumble.appyx.navigation.node.backstack.app.indexedbackstack.IndexedBackStack.State.Destroyed
import com.bumble.appyx.navigation.node.backstack.app.indexedbackstack.IndexedBackStack.State.Stashed
import com.bumble.appyx.navigation.ui.appyx_yellow2
import com.bumble.appyx.navigation.ui.atomic_tangerine
import com.bumble.appyx.navigation.ui.imperial_red
import com.bumble.appyx.core.navigation.NavElement
import java.util.Locale

@Composable
fun <T : Any> PeekInsideBackStack(
    backStack: IndexedBackStack<T>,
    modifier: Modifier = Modifier
) {
    val elements = backStack.elements.collectAsState()

    val listState = rememberLazyListState()
    LaunchedEffect(elements.value.lastIndex) {
        listState.animateScrollToItem(index = elements.value.lastIndex)
    }

    LazyRow(
        state = listState,
        modifier = modifier
            .fillMaxWidth()
            .background(if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        elements.value.forEach { element ->
            item {
                BackStackElement(element)
            }
        }
    }
}

@Composable
private fun <T> BackStackElement(
    element: NavElement<T, IndexedBackStack.State>,
) {
    Column(
        modifier = Modifier
            .size(60.dp)
            .padding(4.dp)
            .clip(RoundedCornerShape(15))
            .background(if (isSystemInDarkTheme()) Color.Transparent else element.targetState.toColor())
            .border(2.dp, element.targetState.toColor(), RoundedCornerShape(15))
            .padding(6.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = element.key.navTarget.toString(),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = element.targetState.javaClass.simpleName.toString()
                .replace("Destroyed", "Destr")
                .uppercase(Locale.getDefault()),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 9.sp,
        )
    }
}

private fun IndexedBackStack.State.toColor(): Color =
    when (this) {
        is Created -> appyx_yellow2
        is Active -> appyx_yellow2
        is Stashed -> atomic_tangerine
        is Destroyed -> imperial_red
    }
