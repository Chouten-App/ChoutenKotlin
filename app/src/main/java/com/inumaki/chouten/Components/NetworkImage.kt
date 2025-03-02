package com.inumaki.chouten.Components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import coil.transition.Transition
import coil.transition.TransitionTarget

@Composable
fun NetworkImage(url: String, modifier: Modifier) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .build(),
        contentScale = ContentScale.Crop,
        contentDescription = null, // Add description if needed,
        modifier = modifier
    )
}