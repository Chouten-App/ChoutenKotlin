package com.inumaki.chouten.Features.Discover

import android.content.res.Resources
import android.util.DisplayMetrics
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.inumaki.chouten.Components.NetworkImage
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.inumaki.chouten.Models.DiscoverData
import com.inumaki.chouten.Models.DiscoverSection
import com.inumaki.chouten.Relay.Relay
import com.inumaki.chouten.ui.theme.ChoutenTheme
import java.net.URLEncoder

@Composable
fun DiscoverView(
    hazeState: HazeState,
    showBanner: Boolean,
    listState: LazyListState,
    viewModel: DiscoverViewModel = viewModel(),
    navController: NavController
) {
    val sections by viewModel.sections.collectAsState()

    if (viewModel.state.value == LoadingState.LOADING) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0C0C0C))
                .hazeSource(state = hazeState),
            verticalArrangement = Arrangement.Top,
            state = listState,
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            item {
                LoadingCarousel()
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0C0C0C))
                .hazeSource(state = hazeState),
            verticalArrangement = Arrangement.Top,
            state = listState,
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            sections?.let { nonNullSections ->
                // Iterate over the non-null list of sections
                items(items = nonNullSections) { section ->
                    when (section.type) {
                        0 -> {
                            Carousel(data = section, showBanner = showBanner, navController = navController)
                        }

                        1 -> {
                            List(
                                title = section.title,
                                list = section.list,
                                navController = navController
                            ) // Pass the list items to List composable
                        }
                        // Handle other possible sections here
                        else -> {
                            Text(text = "Unknown section")
                        }
                    }
                }
            } ?: item {
                // Handle the case where sections is null (e.g., show a placeholder message)
                Text(text = "No sections available")
            }
        }
    }
}

@Composable
fun LoadingCarousel() {
    val loadingListState = rememberLazyListState()

    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState = loadingListState)
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(600.dp),
        contentPadding = PaddingValues(top = 120.dp, start = 40.dp, end = 40.dp),
        flingBehavior = snapFlingBehavior,
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            LoadingCarouselCard()
        }

        item {
            LoadingCarouselCard()
        }

        item {
            LoadingCarouselCard()
        }
    }
}

@Composable
fun LoadingCarouselCard() {
    val displayMetrics: DisplayMetrics = Resources.getSystem().displayMetrics
    val dpWidth = displayMetrics.widthPixels / displayMetrics.density

    Box(
        modifier = Modifier
            .widthIn(min = dpWidth.dp - 80.0.dp, max = dpWidth.dp - 80.0.dp)
            .height(440.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(0.5.dp, Color(0xFF3B3B3B), shape = RoundedCornerShape(20.dp))
            .background(Color(0xFF171717))
    )
}

@Composable
fun List(title: String, list: List<DiscoverData>, navController: NavController) {
    Column(
        modifier = Modifier.padding(top = 12.dp)
    ) {
        Text(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp),
            text = title,
            color = Color(0xFFD4D4D4),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items = list) { item ->
                Column {
                    Box(
                        modifier = Modifier
                            .clickable {
                                println("Clicked on ${item.url}")

                                val encodedUrl = URLEncoder.encode(item.url, "UTF-8")
                                // open info view with data.url
                                navController.navigate(
                                    "InfoView/${encodedUrl}"
                                )
                            },
                        contentAlignment = Alignment.TopEnd
                    ) {
                        NetworkImage(
                            url = item.poster,
                            modifier = Modifier.width(120.dp)
                                .height(180.dp)
                                .clip(RoundedCornerShape(size = 8.dp))
                                .background(Color(0xFF171717))
                                .border(
                                    width = 0.5.dp,
                                    color = Color(0xFF3B3B3B),
                                    shape = RoundedCornerShape(size = 8.dp)
                                )
                        )

                        if (item.indicator != null ) {
                            Box(
                                modifier = Modifier
                                    .padding(6.dp)
                            ) {
                                Text(
                                    item.indicator ?: "",
                                    fontSize = 10.sp,
                                    lineHeight = 1.1.em,
                                    color = ChoutenTheme.colors.fg,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(percent = 50))
                                        .background(Color(0xFF272727))
                                        .border(0.5.dp, Color(0xFF3B3B3B), RoundedCornerShape(percent = 50))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                    }

                    Text(
                        item.titles.primary,
                        fontSize = 12.sp,
                        lineHeight = 1.1.em,
                        maxLines = 2,
                        color = ChoutenTheme.colors.fg,
                        modifier = Modifier
                            .width(120.dp)
                            .padding(start = 8.dp, end = 8.dp, bottom = 4.dp, top = 4.dp)
                    )

                    Text(
                        "${item.current ?: "~"}/${item.total ?: "~"}",
                        fontSize = 10.sp,
                        lineHeight = 1.1.em,
                        color = ChoutenTheme.colors.fg,
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                            .offset(y = (-2).dp)
                            .alpha(0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun Modifier.conditional(condition : Boolean, modifier : @Composable Modifier.() -> Modifier) : Modifier {
    return if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }
}

@Composable
fun Carousel(data: DiscoverSection, showBanner: Boolean, navController: NavController) {
    val state = rememberLazyListState()
    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState = state)
    val index by remember { derivedStateOf { state.firstVisibleItemIndex } }

    // "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?fm=jpg&q=60&w=3000&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8YWJzdHJhY3R8ZW58MHx8MHx8fDA%3D"
    Box {
        if (showBanner) {
            NetworkImage(
                url = data.list[index].banner ?: data.list[index].poster,
                modifier = Modifier
                    .fillMaxSize()
                    .height(620.dp)
                    .blur(20.dp)
            )
        }


        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(620.dp)
                .conditional(showBanner) {
                    background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0x790C0C0C),
                                Color(0xFF0C0C0C)
                            )
                        )
                    )
                },
            verticalAlignment = Alignment.CenterVertically,
            state = state,
            contentPadding = PaddingValues(top = 120.dp, start = 40.dp, end = 40.dp),
            flingBehavior = snapFlingBehavior,
            horizontalArrangement = Arrangement.spacedBy(20.dp)

        ) {
            items(data.list) {
                CarouselCard(data = it, navController = navController)
            }
        }
    }

}

@Composable
fun CarouselCard(data: DiscoverData, navController: NavController) {
    val displayMetrics: DisplayMetrics = Resources.getSystem().displayMetrics
    val dpWidth = displayMetrics.widthPixels / displayMetrics.density

    Box(
        modifier = Modifier
            .widthIn(min = dpWidth.dp - 80.0.dp, max = 600.dp - 80.0.dp)
            .height(440.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(0.5.dp, Color(0xFF3B3B3B), shape = RoundedCornerShape(20.dp))
            .background(Color(0xFF171717))
            .clickable {
                println("Clicked on ${data.url}")

                val encodedUrl = URLEncoder.encode(data.url, "UTF-8")
                // open info view with data.url
                navController.navigate(
                    "InfoView/${encodedUrl}"
                )
            }
    ) {
        NetworkImage(
            url = data.poster,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0x00171717),
                            Color(0xFF171717)
                        )
                    )
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                data.titles.secondary ?: "",
                color = Color(0xB2D4D4D4),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    data.titles.primary,
                    color = Color(0xFFD4D4D4),
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )

                Icon(
                    modifier = Modifier.width(16.dp),
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "",
                    tint = Color(0xFFBB3834)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                data.description,
                color = Color(0xB2D4D4D4),
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )
        }
    }
}