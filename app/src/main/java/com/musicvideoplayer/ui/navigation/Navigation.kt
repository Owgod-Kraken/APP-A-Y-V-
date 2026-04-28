package com.musicvideoplayer.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material.icons.outlined.AudioFile
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.VideoFile
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object MusicList : Screen("music_list")
    data object VideoList : Screen("video_list")
    data object AudioPlayer : Screen("audio_player")
    data object VideoPlayer : Screen("video_player/{mediaId}") {
        fun createRoute(mediaId: Long) = "video_player/$mediaId"
    }
    data object Settings : Screen("settings")
}

data class BottomNavItem(
    val title: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(
        title = "Inicio",
        route = Screen.Home.route,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    BottomNavItem(
        title = "Música",
        route = Screen.MusicList.route,
        selectedIcon = Icons.Filled.AudioFile,
        unselectedIcon = Icons.Outlined.AudioFile
    ),
    BottomNavItem(
        title = "Videos",
        route = Screen.VideoList.route,
        selectedIcon = Icons.Filled.VideoFile,
        unselectedIcon = Icons.Outlined.VideoFile
    ),
    BottomNavItem(
        title = "Ajustes",
        route = Screen.Settings.route,
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )
)
