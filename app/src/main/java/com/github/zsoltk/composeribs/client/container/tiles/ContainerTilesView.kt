//package com.github.zsoltk.composeribs.client.container.tiles
//
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxHeight
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.heightIn
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.widthIn
//import androidx.compose.material.Button
//import androidx.compose.material.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import com.github.zsoltk.composeribs.client.container.Container.Routing
//import com.github.zsoltk.composeribs.client.container.Container.Routing.Child1
//import com.github.zsoltk.composeribs.client.container.Container.Routing.Child2
//import com.github.zsoltk.composeribs.client.container.Container.Routing.Child3
//import com.github.zsoltk.composeribs.client.container.Container.Routing.Child4
//import com.github.zsoltk.composeribs.core.RibComposable
//import com.github.zsoltk.composeribs.core.routing.RoutingKey
//import com.github.zsoltk.composeribs.ui.silver_sand
//import com.github.zsoltk.composeribs.ui.sizzling_red
//
//class ContainerTilesView(
//    private val onSelect: (RoutingKey<Routing>) -> Unit,
//    private val onRemoveSelected: () -> Unit,
//) : RibComposable<Routing>() {
//
//    @Composable
//    override fun Compose() {
//        val modifier: (RoutingKey<Routing>) -> Modifier = {
//            Modifier
//                .widthIn(max = 150.dp)
//                .heightIn(max = 250.dp)
//                .clickable { onSelect(it) }
//        }
//
//        Box(Modifier.fillMaxSize()) {
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .fillMaxHeight(0.6f)
//                    .padding(24.dp),
//            ) {
//                Text("Container")
//                Row(
//                    modifier = Modifier
//                        .weight(1f)
//                        .fillMaxWidth()
//                        .padding(top = 12.dp, bottom = 6.dp),
//                    horizontalArrangement = Arrangement.SpaceEvenly
//                ) {
//
//                    placeholder<Child1>(modifier)
//                    placeholder<Child2>(modifier)
//                }
//                Row(
//                    modifier = Modifier
//                        .weight(1f)
//                        .fillMaxWidth()
//                        .padding(top = 6.dp, bottom = 12.dp),
//                    horizontalArrangement = Arrangement.SpaceEvenly
//                ) {
//                    placeholder<Child3>(modifier)
//                    placeholder<Child4>(modifier)
//
////                placeholder<Routing.Child>(modifier) { it.i % 4 == 3 }
////                placeholder<Routing.Child>(modifier) { it.i % 4 == 2 }
//                }
//
//                Button(onClick = onRemoveSelected) {
//                    Text("Remove selected")
//                }
//            }
//        }
//    }
//}
