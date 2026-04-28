package com.musicvideoplayer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.musicvideoplayer.data.model.MediaItem
import com.musicvideoplayer.ui.components.MiniPlayer
import com.musicvideoplayer.ui.navigation.Screen
import com.musicvideoplayer.ui.navigation.bottomNavItems
import com.musicvideoplayer.ui.screens.home.HomeScreen
import com.musicvideoplayer.ui.screens.home.MusicListScreen
import com.musicvideoplayer.ui.screens.home.VideoListScreen
import com.musicvideoplayer.ui.screens.player.AudioPlayerScreen
import com.musicvideoplayer.ui.screens.settings.SettingsScreen
import com.musicvideoplayer.ui.screens.videoplayer.VideoPlayerScreen
import com.musicvideoplayer.ui.theme.MusicVideoPlayerTheme
import com.musicvideoplayer.viewmodel.MediaViewModel
import com.musicvideoplayer.viewmodel.PlayerViewModel
import com.musicvideoplayer.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            recreate()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!hasMediaPermissions()) {
            requestMediaPermissions()
        }

        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            val themeColor by settingsViewModel.themeColor.collectAsState()
            val themeMode by settingsViewModel.themeMode.collectAsState()

            MusicVideoPlayerTheme(
                themeColor = themeColor,
                themeMode = themeMode
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainApp(settingsViewModel = settingsViewModel)
                }
            }
        }
    }

    private fun hasMediaPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) ==
                PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) ==
                PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestMediaPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        requestPermissionLauncher.launch(permissions)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(settingsViewModel: SettingsViewModel) {
    val navController = rememberNavController()
    val mediaViewModel: MediaViewModel = viewModel()
    val playerViewModel: PlayerViewModel = viewModel()

    val currentItem by playerViewModel.currentItem.collectAsState()
    val isPlaying by playerViewModel.isPlaying.collectAsState()
    val currentPosition by playerViewModel.currentPosition.collectAsState()
    val duration by playerViewModel.duration.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route, Screen.MusicList.route,
        Screen.VideoList.route, Screen.Settings.route
    )
    val showMiniPlayer = currentItem != null && currentItem?.isAudio == true &&
        currentRoute != Screen.AudioPlayer.route &&
        currentRoute != Screen.VideoPlayer.route.substringBefore("/")

    // Load media on start
    var mediaLoaded by remember { mutableStateOf(false) }
    if (!mediaLoaded) {
        mediaViewModel.loadMedia()
        mediaLoaded = true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (currentRoute == item.route) item.selectedIcon
                                    else item.unselectedIcon,
                                    contentDescription = item.title
                                )
                            },
                            label = { Text(item.title) },
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(innerPadding),
                enterTransition = {
                    fadeIn(animationSpec = tween(300)) +
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start,
                        tween(300)
                    )
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(300))
                },
                popEnterTransition = {
                    fadeIn(animationSpec = tween(300))
                },
                popExitTransition = {
                    fadeOut(animationSpec = tween(300)) +
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.End,
                        tween(300)
                    )
                }
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        mediaViewModel = mediaViewModel,
                        onAudioClick = { item, playlist ->
                            playerViewModel.setPlaylist(playlist, playlist.indexOf(item))
                            navController.navigate(Screen.AudioPlayer.route)
                        },
                        onVideoClick = { item ->
                            playerViewModel.playItem(item)
                            navController.navigate(
                                Screen.VideoPlayer.createRoute(item.id)
                            )
                        }
                    )
                }

                composable(Screen.MusicList.route) {
                    MusicListScreen(
                        mediaViewModel = mediaViewModel,
                        onAudioClick = { item, playlist ->
                            playerViewModel.setPlaylist(playlist, playlist.indexOf(item))
                            navController.navigate(Screen.AudioPlayer.route)
                        }
                    )
                }

                composable(Screen.VideoList.route) {
                    VideoListScreen(
                        mediaViewModel = mediaViewModel,
                        onVideoClick = { item ->
                            playerViewModel.playItem(item)
                            navController.navigate(
                                Screen.VideoPlayer.createRoute(item.id)
                            )
                        }
                    )
                }

                composable(Screen.AudioPlayer.route) {
                    AudioPlayerScreen(
                        playerViewModel = playerViewModel,
                        mediaViewModel = mediaViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.VideoPlayer.route) {
                    VideoPlayerScreen(
                        playerViewModel = playerViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.Settings.route) {
                    SettingsScreen(settingsViewModel = settingsViewModel)
                }
            }

            // Mini Player
            if (showMiniPlayer) {
                MiniPlayer(
                    currentItem = currentItem,
                    isPlaying = isPlaying,
                    progress = if (duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f,
                    onPlayPause = { playerViewModel.playPause() },
                    onNext = { playerViewModel.playNext() },
                    onClick = { navController.navigate(Screen.AudioPlayer.route) },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = if (showBottomBar) innerPadding.calculateBottomPadding() else 0.dp)
                )
            }
        }
    }
}
