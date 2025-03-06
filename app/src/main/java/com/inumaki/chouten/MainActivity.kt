package com.inumaki.chouten

import PackageIcon
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsIgnoringVisibility
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LaptopMac
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.inumaki.chouten.Components.NetworkImage
import com.inumaki.chouten.Features.Discover.DiscoverView
import com.inumaki.chouten.Features.Discover.DiscoverViewModel
import com.inumaki.chouten.Features.Discover.conditional
import com.inumaki.chouten.Features.Info.InfoView
import com.inumaki.chouten.Features.Settings.Developer.DeveloperView
import com.inumaki.chouten.Features.Settings.SettingsView
import com.inumaki.chouten.Relay.Relay
import com.inumaki.chouten.ui.theme.ChoutenTheme
import com.inumaki.chouten.ui.theme.LocalDeviceInfo
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import kotlinx.coroutines.launch

// import com.dokar.quickJs.QuickJS

data class TabBarItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeAmount: Int? = null
)

fun Modifier.topBorder(
    color: Color,
    height: Float,
) = this.drawWithContent {
    drawContent()
    drawLine(
        color = color,
        start = Offset(0f, 0f),
        end = Offset(size.width, 0f),
        strokeWidth = height,
    )
}

fun Modifier.bottomBorder(
    color: Color,
    height: Float,
) = this.drawWithContent {
    drawContent()
    drawLine(
        color = color,
        start = Offset(0f, size.height),
        end = Offset(size.width, size.height),
        strokeWidth = height,
    )
}

fun getAppVersion(context: Context): String {
    return try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        packageInfo.versionName ?: "Unknown"
    } catch (e: PackageManager.NameNotFoundException) {
        "Unknown"
    }
}

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        Relay.setContext(applicationContext)
        Relay.initialize()

        setContent {
            val systemUiController = rememberSystemUiController()

            // Set the status bar and navigation bar colors
            systemUiController.setSystemBarsColor(
                color = Color.Transparent
            )

            systemUiController.setNavigationBarColor(
                color = Color.Transparent
            )

            // setting up the individual tabs
            val homeTab = TabBarItem(title = "Home", selectedIcon = Icons.Filled.Home, unselectedIcon = Icons.Outlined.Home)
            val alertsTab = TabBarItem(title = "Discover", selectedIcon = Icons.Filled.Explore, unselectedIcon = Icons.Outlined.Explore)
            val settingsTab = TabBarItem(title = "Repos", selectedIcon = PackageIcon, unselectedIcon = PackageIcon)

            // creating a list of all the tabs
            val tabBarItems = listOf(homeTab, alertsTab, settingsTab)

            // creating our navController
            val navController = rememberNavController()
            val mainNavController = rememberNavController()

            val hazeState = remember { HazeState() }

            val discoverViewModel = viewModel<DiscoverViewModel>()
            val listState = rememberLazyListState()

            val sharedPreferences = applicationContext.getSharedPreferences("Preferences", Context.MODE_PRIVATE)

            var showBanner by remember { mutableStateOf(sharedPreferences.getBoolean("showCarouselBanner", false)) }
            var showSheet by remember { mutableStateOf(false) }
            val sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            )

            // animation variable
            val animatedAlpha by animateFloatAsState(
                targetValue = if (showSheet) 1.0f else 0f,
                label = "alpha"
            )

            val animatedScale by animateFloatAsState(
                targetValue = if (showSheet) 0.9f else 1.0f,
                label = "scale"
            )

            // folder permissions
            val sharedPrefs = applicationContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val folderUriString = sharedPrefs.getString("folder_uri", null)

            val folderPickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.OpenDocumentTree()
            ) { uri: Uri? ->
                uri?.let {
                    onFolderSelected(it)
                }
            }

            LaunchedEffect(key1 = true) {
                if (folderUriString == null) {
                    folderPickerLauncher.launch(null)
                }
            }

            ChoutenTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ChoutenTheme.colors.background
                ) {
                    if (!LocalDeviceInfo.current.isTablet) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF000000))
                        )
                    }

                    NavHost(mainNavController, "homeScreens") {
                        composable("homeScreens") {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.TopStart
                            ) {
                                Scaffold(
                                    modifier = Modifier
                                        .scale(animatedScale)
                                        .clip(RoundedCornerShape(if (showSheet) 20.dp else 0.dp)),
                                    contentWindowInsets = WindowInsets.systemBarsIgnoringVisibility,
                                    topBar = {
                                        AppTopBar(
                                            hazeState = hazeState,
                                            navController = navController,
                                            listState = listState
                                        ) {
                                            showSheet = true
                                        }
                                    },
                                    bottomBar = {
                                        if (!LocalDeviceInfo.current.isTablet) {
                                            TabView(tabBarItems, navController)
                                        }
                                    }
                                ) {
                                    NavHost(navController = navController, startDestination = homeTab.title) {
                                        composable(homeTab.title) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(Color(0xFF0C0C0C)),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(20.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text("Show discover banner")

                                                    Switch(
                                                        checked = showBanner,
                                                        onCheckedChange = {
                                                            // Update the state
                                                            showBanner = it

                                                            val sharedPreferences = applicationContext.getSharedPreferences("Preferences", Context.MODE_PRIVATE)
                                                            val editor = sharedPreferences.edit()
                                                            editor.putBoolean("showCarouselBanner", it)
                                                            editor.apply()
                                                        }
                                                    )
                                                }

                                                Text(
                                                    homeTab.title,
                                                )

                                                Text(
                                                    "Device is ${ if (LocalDeviceInfo.current.isTablet) "Tablet" else "Phone" }",
                                                    color = ChoutenTheme.colors.fg
                                                )
                                            }
                                        }
                                        composable(alertsTab.title) {
                                            DiscoverView(hazeState = hazeState, showBanner = showBanner, listState = listState, viewModel = discoverViewModel, navController = mainNavController)
                                        }
                                        composable(
                                            settingsTab.title
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(Color(0xFF0C0C0C)),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                JSRunner()
                                            }
                                        }
                                    }

                                }

                                if (LocalDeviceInfo.current.isTablet) {
                                    TabView(tabBarItems, navController)
                                }
                            }
                        }
                        // Info View
                        composable(
                            "InfoView/{url}",
                            arguments = listOf(
                                navArgument("url") {
                                    type = NavType.StringType
                                }
                            )
                        ) { entry ->
                            InfoView(url = entry.arguments?.getString("url") ?: "")
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp, vertical = 45.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .graphicsLayer {
                                alpha = animatedAlpha
                            }
                            .background(Color(0x19FFFFFF))
                    ) {  }

                    if (showSheet) {
                        val sheetNavController = rememberNavController() // Separate NavController

                        val backStackEntry by sheetNavController.currentBackStackEntryAsState()
                        val screenName = backStackEntry?.destination?.route

                        ModalBottomSheet(
                            onDismissRequest = { showSheet = false },
                            sheetState = sheetState,
                            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                            containerColor = Color(0xFF0C0C0C),
                            dragHandle = {},
                        ) {
                            Box(
                                modifier = Modifier.fillMaxWidth()
                                    .fillMaxHeight(fraction = 0.92f)
                                    .padding(16.dp)
                            ) {
                                NavHost(
                                    modifier = Modifier.fillMaxSize()
                                        .padding(top = 40.dp),
                                    navController = sheetNavController,
                                    startDestination = "Settings" // Default screen inside the sheet
                                ) {
                                    composable(
                                        "Settings",
                                        enterTransition = {
                                            slideIntoContainer(
                                                AnimatedContentTransitionScope.SlideDirection.Right,
                                                animationSpec = tween(400)
                                            )
                                        },
                                        exitTransition = {
                                            slideOutOfContainer(
                                                AnimatedContentTransitionScope.SlideDirection.Left,
                                                animationSpec = tween(400)
                                            )
                                        }
                                    ) {
                                        SettingsView(
                                            context = applicationContext,
                                            onNavigate = { sheetNavController.navigate("Developer") }
                                        )
                                    }
                                    composable(
                                        "Developer",
                                        enterTransition = {
                                            slideIntoContainer(
                                                AnimatedContentTransitionScope.SlideDirection.Left,
                                                animationSpec = tween(400)
                                            )
                                        },
                                        exitTransition = {
                                            slideOutOfContainer(
                                                AnimatedContentTransitionScope.SlideDirection.Right,
                                                animationSpec = tween(400)
                                            )
                                        },
                                        popExitTransition = {
                                            slideOutOfContainer(
                                                AnimatedContentTransitionScope.SlideDirection.Right,
                                                animationSpec = tween(400)
                                            )
                                        }
                                    ) {
                                        DeveloperView()
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (screenName == "Settings") "Done" else "Back",
                                        modifier = Modifier
                                            .clickable {
                                                if (screenName == "Settings") {
                                                    showSheet = false
                                                } else {
                                                    sheetNavController.popBackStack()
                                                }
                                            },
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFD4D4D4)
                                    )

                                    Text(
                                        screenName ?: "Settings",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFD4D4D4)
                                    )

                                    Text(
                                        text = if (screenName == "Settings") "Done" else "Back",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0x00D4D4D4)
                                    )
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    private fun onFolderSelected(uri: Uri) {
        val contentResolver = applicationContext.contentResolver
        val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        contentResolver.takePersistableUriPermission(uri, takeFlags)

        // Save URI in SharedPreferences
        applicationContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE).edit()
            .putString("folder_uri", uri.toString())
            .apply()
    }
}

@Composable
fun SettingsLink(icon: ImageVector, title: String, onTap: () -> Unit?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onTap()
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(32.dp)
                    .height(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF272727))
                    .border(
                        width = 0.5.dp,
                        color = Color(0xFF3B3B3B),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.width(16.dp),
                    imageVector = icon,
                    contentDescription = "",
                    tint = Color(0xFFD4D4D4)
                )
            }

            Text(
                title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFFD4D4D4)
            )
        }

        Icon(
            modifier = Modifier.height(24.dp),
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = "",
            tint = Color(0xFFD4D4D4)
        )
    }
}

@Composable
fun JSRunner() {
    val context = rememberCoroutineScope()
    var jsCode by remember {
        mutableStateOf("""
            
        """.trimIndent())
    }
    var result by remember { mutableStateOf("") }


    Column {
        TextField(
            value = jsCode,
            onValueChange = { jsCode = it },
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
        )

        Row {
            Button(
                onClick = {
                    context.launch {
                        result = ""
//                        Relay.executeJS(jsCode)
//
//                        Relay.callFunction("fetchData", "https://jsonplaceholder.typicode.com/todos/1") {
//                            println("Received: $it")
//                            result = it
//                        }
                    }
                }
            ) {
                Text("Play")
            }
            Button(onClick = { jsCode = "1 + 1" }) {
                Text("Reset")
            }
        }
        Text(
            "Result: $result",
            maxLines = 7
        )
    }
}

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun AppTopBar(
    hazeState: HazeState,
    listState: LazyListState,
    navController: NavController,
    buttonTapped: () -> Unit
) {
    val blurStyle = HazeMaterials.ultraThin(
        containerColor = Color(0xFF0C0C0C)
    )

    val backStackEntry by navController.currentBackStackEntryAsState()
    val screenName = backStackEntry?.destination?.route

    val startOffset = if (LocalDeviceInfo.current.isTablet) 130.dp else 20.dp

    val hazeAlpha by remember {
        derivedStateOf {
            val scrollOffset = listState.firstVisibleItemScrollOffset
            val alpha = scrollOffset / 200f // Adjust divisor for desired effect

            alpha.coerceIn(0f, 1f) // Ensures alpha stays between 0 and 1
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .hazeEffect(state = hazeState) {
                backgroundColor = Color(0xFF0C0C0C)
                blurEnabled = true
                blurRadius = (hazeAlpha * 40f).dp // 40.dp
                progressive = HazeProgressive.verticalGradient(
                    startIntensity = 1f,
                    endIntensity = 0f
                )
            }
            .padding(start = startOffset, end = 20.dp, top = 60.dp, bottom = 50.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = screenName ?: "",
            color = Color(0xFFD4D4D4),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        when (screenName) {
            "Home" -> {
                CircleButton(icon = Icons.Outlined.PersonOutline) {
                    buttonTapped()
                }
            }
            "Repos" -> {
                CircleButton(icon = Icons.Outlined.Add) {

                }
            }
            else -> {
                CircleButton(icon = Icons.Outlined.Search) {

                }
            }
        }
    }
}

@Composable
fun CircleButton(
    icon: ImageVector,
    sizeModifier: Float? = null,
    modifier: Modifier = Modifier,
    onTap: () -> Unit?
) {
    Box(
        modifier = modifier
            .width(32.dp)
            .height(32.dp)
            .clip(CircleShape)
            .background(Color(0xFF272727))
            .border(
                width = 0.5.dp,
                color = Color(0xFF3B3B3B),
                shape = CircleShape
            )
            .clickable { onTap() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.width(16.dp).scale(sizeModifier ?: 1.0f),
            imageVector = icon,
            contentDescription = "",
            tint = Color(0xFFD4D4D4)
        )
    }
}

// ----------------------------------------
// This is a wrapper view that allows us to easily and cleanly
// reuse this component in any future project
@Composable
fun TabView(tabBarItems: List<TabBarItem>, navController: NavController) {
    var selectedTabIndex by rememberSaveable {
        mutableStateOf(0)
    }

    val displayMetrics: DisplayMetrics = Resources.getSystem().displayMetrics
    val dpWidth = displayMetrics.widthPixels / displayMetrics.density

    val animatedOffset by animateDpAsState(
        targetValue = when(selectedTabIndex) {
            0 -> { 45.0.dp }
            1 -> { ((dpWidth.toFloat() / 2) - 12).dp }
            2 -> { (dpWidth.toFloat() - 45 - 24).dp }
            else -> {
                0.0.dp
            }
        },
        label = "offset"
    )

    if (LocalDeviceInfo.current.isTablet) {
        Column(
            modifier = Modifier
                .padding(horizontal = 30.dp, vertical = 50.dp)
                .clip(RoundedCornerShape(50))
                .background(ChoutenTheme.colors.container)
                .border(
                    0.5.dp,
                    ChoutenTheme.colors.border,
                    shape = RoundedCornerShape(50)
                )
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NetworkImage(
                url = "https://wallpapers.com/images/hd/cute-blushing-boy-anime-discord-pfp-304ry32l6uz09ybd.jpg",
                modifier = Modifier
                    .width(48.dp)
                    .height(48.dp)
                    .clip(CircleShape)
                    .border(
                        0.5.dp,
                        ChoutenTheme.colors.border,
                        shape = CircleShape
                    )
            )

            tabBarItems.forEachIndexed { index, tabBarItem ->
                Icon(
                    imageVector = if (index == selectedTabIndex) tabBarItem.selectedIcon else tabBarItem.unselectedIcon,
                    contentDescription = tabBarItem.title,
                    tint = ChoutenTheme.colors.fg,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (index == selectedTabIndex) ChoutenTheme.colors.overlay else Color.Transparent)
                        .conditional(
                            index == selectedTabIndex
                        ) {
                            Modifier
                                .border(
                                    0.5.dp,
                                    ChoutenTheme.colors.border,
                                    shape = CircleShape
                                )
                        }
                        .padding(12.dp)
                        .alpha(if (index == selectedTabIndex) 1.0f else 0.7f)
                        .clickable {
                            selectedTabIndex = index
                            navController.navigate(tabBarItem.title)
                        }
                )
            }

            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = "Settings",
                tint = ChoutenTheme.colors.fg,
                modifier = Modifier
                    .clip(CircleShape)
                    .padding(12.dp)
                    .alpha(0.7f)
            )
        }
    } else {
        Box {
            NavigationBar(
                containerColor = Color(0xFF171717),
                modifier = Modifier.topBorder(
                    color = Color(59, 59, 59, 255),
                    height = 1f
                ).background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            ChoutenTheme.colors.background,
                            Color.Transparent
                        )
                    )
                )
            ) {
                // looping over each tab to generate the views and navigation for each item
                tabBarItems.forEachIndexed { index, tabBarItem ->
                    NavigationBarItem(
                        selected = selectedTabIndex == index,
                        onClick = {
                            selectedTabIndex = index
                            navController.navigate(tabBarItem.title)
                        },
                        icon = {
                            TabBarIconView(
                                isSelected = selectedTabIndex == index,
                                selectedIcon = tabBarItem.selectedIcon,
                                unselectedIcon = tabBarItem.unselectedIcon,
                                title = tabBarItem.title,
                                badgeAmount = tabBarItem.badgeAmount
                            )
                        },
                        label = {Text(tabBarItem.title)},
                        colors = NavigationBarItemColors(
                            selectedTextColor = Color(0.8313725f, 0.8313725f, 0.8313725f, 1.0f),
                            selectedIconColor = Color(0.8313725f, 0.8313725f, 0.8313725f, 1.0f),
                            unselectedTextColor = Color(0.8313725f, 0.8313725f, 0.8313725f, 0.7f),
                            unselectedIconColor = Color(0.8313725f, 0.8313725f, 0.8313725f, 0.7f),
                            selectedIndicatorColor = Color.Transparent,
                            disabledIconColor = Color.Transparent,
                            disabledTextColor = Color.Transparent,
                        )
                    )
                }
            }

            Box(
                modifier = Modifier
                    .width(24.dp)
                    .height(4.dp)
                    .offset(x = animatedOffset, y = -2.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF6458ED))
            )
        }
    }
}

// This component helps to clean up the API call from our TabView above,
// but could just as easily be added inside the TabView without creating this custom component
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabBarIconView(
    isSelected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    title: String,
    badgeAmount: Int? = null
) {
    BadgedBox(badge = { TabBarBadgeView(badgeAmount) }) {
        Icon(
            imageVector = if (isSelected) {selectedIcon} else {unselectedIcon},
            contentDescription = title
        )
    }
}

// This component helps to clean up the API call from our TabBarIconView above,
// but could just as easily be added inside the TabBarIconView without creating this custom component
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TabBarBadgeView(count: Int? = null) {
    if (count != null) {
        Badge {
            Text(count.toString())
        }
    }
}
// end of the reusable components that can be copied over to any new projects
// ----------------------------------------

// This was added to demonstrate that we are infact changing views when we click a new tab
@Composable
fun MoreView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Thing 1")
        Text("Thing 2")
        Text("Thing 3")
        Text("Thing 4")
        Text("Thing 5")
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    val homeTab = TabBarItem(title = "Home", selectedIcon = Icons.Filled.Home, unselectedIcon = Icons.Outlined.Home)
    val alertsTab = TabBarItem(title = "Alerts", selectedIcon = Icons.Filled.Notifications, unselectedIcon = Icons.Outlined.Notifications, badgeAmount = 7)
    val settingsTab = TabBarItem(title = "Settings", selectedIcon = Icons.Filled.Settings, unselectedIcon = Icons.Outlined.Settings)

    // creating a list of all the tabs
    val tabBarItems = listOf(homeTab, alertsTab, settingsTab)

    val navController = rememberNavController()

    ChoutenTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(bottomBar = { TabView(tabBarItems, navController) }) {
                NavHost(navController = navController, startDestination = homeTab.title) {
                    composable(homeTab.title) {
                        Text(homeTab.title)
                    }
                    composable(alertsTab.title) {
                        Text(alertsTab.title)
                    }
                    composable(settingsTab.title) {
                        Text(settingsTab.title)
                    }
                }
            }
        }
    }
}