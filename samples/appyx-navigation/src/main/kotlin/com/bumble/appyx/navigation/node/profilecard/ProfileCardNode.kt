package com.bumble.appyx.navigation.node.profilecard

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.samples.common.profile.Profile

class ProfileCardNode(
    buildContext: BuildContext,
    private val profile: Profile
) : Node(buildContext) {

    @Composable
    @Override
    override fun View(modifier: Modifier) {
        ProfileCard(
            profile = profile,
            modifier = modifier
        )
    }
}

@Composable
fun ProfileCard(
    profile: Profile,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        if (maxHeight < threshold) {
            ProfileImage(profile.drawableRes)
        } else {
            ProfileImage(profile.drawableRes)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 24.dp, bottom = 36.dp)
            ) {
                Text(
                    text = "${profile.name}, ${profile.age}",
                    color = Color.White,
                    fontSize = 30.sp
                )
                Spacer(modifier = Modifier.requiredHeight(4.dp))
                Text(text = profile.city, color = Color.White, fontSize = 20.sp)
            }
        }
    }
}

@Composable
fun ProfileImage(data: Any?, modifier: Modifier = Modifier) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(data)
            .crossfade(true)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp))
    )
}

private val threshold = 600.dp
