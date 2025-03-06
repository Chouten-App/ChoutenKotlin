package com.inumaki.chouten.Features.Player

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Forward5
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerControlView
import androidx.media3.ui.PlayerView
import com.inumaki.chouten.R
import com.inumaki.chouten.ui.theme.ChoutenTheme
import dev.vivvvek.seeker.Seeker

@Composable
fun PlayerView(
    url: String
) {
    val context = LocalContext.current

    val exoPlayer = rememberExoPlayer(url)

    Box(modifier = Modifier) {
        DisposableEffect(key1 = Unit) { onDispose { exoPlayer.release() } }

        AndroidView(
            factory = {
                PlayerView(context).apply {
                    player = exoPlayer
                    layoutParams =
                        FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    useController = false
                }
            }
        )

        CustomExoPlayerUI(player = exoPlayer)
    }
}

@OptIn(UnstableApi::class)
@Composable
fun rememberExoPlayer(uri: String): ExoPlayer {
    val context = LocalContext.current
    val renderersFactory = DefaultRenderersFactory(context)
        .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF)
        .setEnableDecoderFallback(true)

    return remember {
        ExoPlayer.Builder(context, renderersFactory).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
            playWhenReady = true
        }
    }
}

@Composable
fun CustomExoPlayerUI(player: ExoPlayer, modifier: Modifier = Modifier) {
    // Custom Play/Pause Button
    var isPlaying by remember { mutableStateOf(player.isPlaying) }

    LaunchedEffect(player.isPlaying) { isPlaying = player.isPlaying }

    Box(
        modifier = modifier
            .background(Color.Black.copy(0.6f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CenterControls(
                modifier = modifier
                    .fillMaxWidth(),
                isPlaying = { isPlaying },
                onReplayClick = { player.seekBack() },
                onForwardClick = { player.seekForward() },
                onPauseToggle = {
                    if (player.isPlaying) {
                        // pause the video
                        player.pause()
                    } else {
                        // play the video
                        // it's already paused
                        player.play()
                    }
                    isPlaying = isPlaying.not()
                }
            )

            BottomControls()
        }
    }
}

@Composable
fun CenterControls(
    modifier: Modifier = Modifier,
    isPlaying: () -> Boolean,
    onReplayClick: () -> Unit,
    onPauseToggle: () -> Unit,
    onForwardClick: () -> Unit
) {
    val isVideoPlaying = remember(isPlaying()) { isPlaying() }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        //replay button
        IconButton(modifier = Modifier.size(34.dp), onClick = {onReplayClick()}) {
            Icon(
                modifier = Modifier.fillMaxSize(),
                imageVector = Icons.Filled.Forward10,
                contentDescription = "Replay 5 seconds",
                tint = ChoutenTheme.colors.fg
            )
        }

        //pause/play toggle button
        IconButton(modifier = Modifier.size(52.dp), onClick = {onPauseToggle()}) {
            Icon(
                modifier = Modifier
                    .fillMaxSize(),
                imageVector = if (isVideoPlaying) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                contentDescription = "Play/Pause",
                tint = ChoutenTheme.colors.fg
            )
        }

        //forward button
        IconButton(modifier = Modifier.size(34.dp), onClick = {onForwardClick()}) {
            Icon(
                modifier = Modifier.fillMaxSize(),
                imageVector = Icons.Filled.Forward10,
                contentDescription = "Forward 10 seconds",
                tint = ChoutenTheme.colors.fg
            )
        }
    }
}

@Composable
fun BottomControls() {
    var value by remember { mutableFloatStateOf(0f) }
    val readAheadValue by remember { mutableFloatStateOf(50f) }

    Column(
        modifier = Modifier
            .padding(horizontal = 80.dp)
            .fillMaxWidth()
    ) {
        Seeker(
            value = value,
            readAheadValue = readAheadValue,
            range = 1f..100f,
            onValueChange = { value = it }
        )
    }
}
