package com.inumaki.chouten.Features.Info

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradient
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.inumaki.chouten.CircleButton
import com.inumaki.chouten.Components.NetworkImage
import com.inumaki.chouten.Features.Discover.DiscoverViewModel
import com.inumaki.chouten.Features.Player.VideoPlayerActivity
import com.inumaki.chouten.ui.theme.ChoutenTheme
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials

@Composable
fun VariableBlur(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height = 175.dp)
            .blur(
                radius = 50.dp,
                edgeTreatment = BlurredEdgeTreatment.Unbounded
            )
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)
@Composable
fun InfoView(
    url: String
) {
    val context = LocalContext.current

    val hazeState = remember { HazeState() }

    val hazeBackground = ChoutenTheme.colors.background

    val viewModel: InfoViewModel = viewModel(
        factory = InfoViewModelFactory(url)
    )

    val infoData by viewModel.infoData.collectAsState()

    Box {
        if (infoData != null) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
                    .background(ChoutenTheme.colors.background)
                    .hazeSource(state = hazeState)
            ) {
                // Top Part
                item {
                    Box {
                        NetworkImage(
                            url = infoData!!.banner ?: infoData!!.poster,
                            modifier = Modifier.fillMaxWidth()
                                .height(320.dp)
                        )

                        Column(
                            modifier = Modifier.fillMaxWidth()
                                .height(380.dp)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0x790C0C0C),
                                            Color(0xE50C0C0C)
                                        ),
                                        endY = 320f
                                    )
                                ),
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 20.dp),
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                NetworkImage(
                                    url = infoData!!.poster,
                                    modifier = Modifier
                                        .width(120.dp)
                                        .height(180.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(0.5.dp, Color(0xFF3B3B3B), shape = RoundedCornerShape(8.dp))
                                )

                                Column(
                                    modifier = Modifier.padding(bottom = 12.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        infoData!!.titles.primary,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFD4D4D4)
                                    )

                                    Icon(
                                        Icons.Filled.Bookmark,
                                        "Bookmark",
                                        tint = Color(0xFFD4D4D4),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF5E5CE6))
                                            .padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Extra Info
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 20.dp)
                    ) {
                        Text(
                            infoData!!.description,
                            modifier = Modifier.alpha(0.7f),
                            color = Color(0xFFD4D4D4),
                            fontSize = 15.sp,
                            lineHeight = 1.1.em,
                            maxLines = 9,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Seasons
                item {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(-4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                infoData!!.seasons.first().name,
                                color = ChoutenTheme.colors.fg,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            CircleButton(Icons.Filled.ChevronRight, sizeModifier = 1.2f, modifier = Modifier.scale(0.8f)) { }
                        }

                        Text(
                            "12 Episodes",
                            modifier = Modifier.alpha(0.7f),
                            color = ChoutenTheme.colors.fg,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Media
                infoData!!.mediaList.firstOrNull()?.pagination?.firstOrNull()?.let {
                    items(items = it.items) { mediaItem ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp, end = 20.dp, bottom = 12.dp)
                                .clip(RoundedCornerShape(size = 12.dp))
                                .background(ChoutenTheme.colors.container)
                                .border(
                                    0.5.dp,
                                    ChoutenTheme.colors.border,
                                    RoundedCornerShape(size = 12.dp)
                                )
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .clickable {
                                    val intent = Intent(context, VideoPlayerActivity::class.java).apply {
                                        putExtra("url", mediaItem.url)
                                    }
                                    context.startActivity(intent)
                                }
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (mediaItem.thumbnail != null) {
                                        NetworkImage(
                                            url = mediaItem.thumbnail,
                                            modifier = Modifier
                                                .width((70 / 9 * 16).dp)
                                                .height(70.dp)
                                                .clip(RoundedCornerShape(size = 8.dp))
                                                .border(
                                                    0.5.dp,
                                                    ChoutenTheme.colors.border,
                                                    RoundedCornerShape(size = 8.dp)
                                                )
                                        )
                                    }

                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            mediaItem.title ?: "Episode ${mediaItem.number}",
                                            color = ChoutenTheme.colors.fg,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Text(
                                            "Episode ${mediaItem.number}",
                                            modifier = Modifier.alpha(0.7f),
                                            color = ChoutenTheme.colors.fg,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                if (mediaItem.description != null) {
                                    Text(
                                        mediaItem.description,
                                        modifier = Modifier.alpha(0.7f),
                                        color = ChoutenTheme.colors.fg,
                                        fontSize = 12.sp,
                                        lineHeight = 1.1.em,
                                        maxLines = 4,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // TopBar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .hazeEffect(state = hazeState) {
                        backgroundColor = hazeBackground
                        blurEnabled = true
                        blurRadius = 50.dp
                        progressive = HazeProgressive.verticalGradient(
                            startIntensity = 1f,
                            endIntensity = 0f
                        )
                    }
                    .padding(start = 20.dp, end = 20.dp, top = 60.dp, bottom = 50.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircleButton(Icons.Filled.ChevronLeft, sizeModifier = 1.1f) { }

                Text(
                    infoData!!.titles.primary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD4D4D4)
                )
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Loading", color = ChoutenTheme.colors.fg)
            }
        }
    }
}