package com.musicvideoplayer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.musicvideoplayer.data.model.AppThemeColor
import com.musicvideoplayer.data.model.ThemeMode
import com.musicvideoplayer.data.preferences.AppPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = AppPreferences(application)

    val themeColor = preferences.themeColor.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), AppThemeColor.BLUE
    )

    val themeMode = preferences.themeMode.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeMode.SYSTEM
    )

    fun setThemeColor(color: AppThemeColor) {
        viewModelScope.launch { preferences.setThemeColor(color) }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { preferences.setThemeMode(mode) }
    }
}
