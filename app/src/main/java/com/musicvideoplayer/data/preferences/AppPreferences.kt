package com.musicvideoplayer.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.musicvideoplayer.data.model.AppThemeColor
import com.musicvideoplayer.data.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

class AppPreferences(private val context: Context) {

    companion object {
        private val THEME_COLOR_KEY = stringPreferencesKey("theme_color")
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        private val FAVORITE_IDS_KEY = stringSetPreferencesKey("favorite_ids")
    }

    val themeColor: Flow<AppThemeColor> = context.dataStore.data.map { prefs ->
        val name = prefs[THEME_COLOR_KEY] ?: AppThemeColor.BLUE.name
        try { AppThemeColor.valueOf(name) } catch (_: Exception) { AppThemeColor.BLUE }
    }

    val themeMode: Flow<ThemeMode> = context.dataStore.data.map { prefs ->
        val name = prefs[THEME_MODE_KEY] ?: ThemeMode.SYSTEM.name
        try { ThemeMode.valueOf(name) } catch (_: Exception) { ThemeMode.SYSTEM }
    }

    val favoriteIds: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[FAVORITE_IDS_KEY] ?: emptySet()
    }

    suspend fun setThemeColor(color: AppThemeColor) {
        context.dataStore.edit { it[THEME_COLOR_KEY] = color.name }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[THEME_MODE_KEY] = mode.name }
    }

    suspend fun toggleFavorite(mediaId: Long) {
        context.dataStore.edit { prefs ->
            val current = prefs[FAVORITE_IDS_KEY]?.toMutableSet() ?: mutableSetOf()
            val idStr = mediaId.toString()
            if (current.contains(idStr)) current.remove(idStr) else current.add(idStr)
            prefs[FAVORITE_IDS_KEY] = current
        }
    }

    suspend fun isFavorite(mediaId: Long): Boolean {
        var result = false
        context.dataStore.edit { prefs ->
            result = prefs[FAVORITE_IDS_KEY]?.contains(mediaId.toString()) == true
        }
        return result
    }
}
