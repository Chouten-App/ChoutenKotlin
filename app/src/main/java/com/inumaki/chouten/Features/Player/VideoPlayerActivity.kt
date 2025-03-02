package com.inumaki.chouten.Features.Player

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.inumaki.chouten.ui.theme.ChoutenTheme

class VideoPlayerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val url = intent.getStringExtra("url") ?: return finish()

        setContent {
            ChoutenTheme {
                PlayerView(url)
            }
        }
    }
}
