package com.musicvideoplayer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.musicvideoplayer.data.model.MediaItem
import com.musicvideoplayer.data.model.MediaType
import com.musicvideoplayer.data.model.SortOrder
import com.musicvideoplayer.data.preferences.AppPreferences
import com.musicvideoplayer.data.repository.MediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MediaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MediaRepository(application)
    private val preferences = AppPreferences(application)

    private val _audioFiles = MutableStateFlow<List<MediaItem>>(emptyList())
    val audioFiles: StateFlow<List<MediaItem>> = _audioFiles.asStateFlow()

    private val _videoFiles = MutableStateFlow<List<MediaItem>>(emptyList())
    val videoFiles: StateFlow<List<MediaItem>> = _videoFiles.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.NAME_ASC)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    private val _showFavoritesOnly = MutableStateFlow(false)
    val showFavoritesOnly: StateFlow<Boolean> = _showFavoritesOnly.asStateFlow()

    val favoriteIds = preferences.favoriteIds

    fun loadMedia() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val favIds = preferences.favoriteIds.first()
                val query = _searchQuery.value
                val sort = _sortOrder.value

                val audio = repository.getMediaItems(MediaType.AUDIO, sort, query, favIds)
                val video = repository.getMediaItems(MediaType.VIDEO, sort, query, favIds)

                _audioFiles.value = if (_showFavoritesOnly.value) {
                    audio.filter { it.isFavorite }
                } else audio

                _videoFiles.value = if (_showFavoritesOnly.value) {
                    video.filter { it.isFavorite }
                } else video
            } catch (_: SecurityException) {
                _audioFiles.value = emptyList()
                _videoFiles.value = emptyList()
            } catch (_: Exception) {
                _audioFiles.value = emptyList()
                _videoFiles.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        loadMedia()
    }

    fun updateSortOrder(order: SortOrder) {
        _sortOrder.value = order
        loadMedia()
    }

    fun toggleFavoritesOnly() {
        _showFavoritesOnly.value = !_showFavoritesOnly.value
        loadMedia()
    }

    fun toggleFavorite(mediaItem: MediaItem) {
        viewModelScope.launch {
            preferences.toggleFavorite(mediaItem.id)
            loadMedia()
        }
    }
}
