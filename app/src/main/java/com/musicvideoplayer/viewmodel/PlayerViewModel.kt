package com.musicvideoplayer.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem as ExoMediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.musicvideoplayer.data.model.MediaItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    val exoPlayer: ExoPlayer = try {
        ExoPlayer.Builder(application).build()
    } catch (e: Exception) {
        ExoPlayer.Builder(application)
            .setHandleAudioBecomingNoisy(false)
            .build()
    }

    private val _currentItem = MutableStateFlow<MediaItem?>(null)
    val currentItem: StateFlow<MediaItem?> = _currentItem.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private val _playlist = MutableStateFlow<List<MediaItem>>(emptyList())
    val playlist: StateFlow<List<MediaItem>> = _playlist.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val _repeatMode = MutableStateFlow(Player.REPEAT_MODE_OFF)
    val repeatMode: StateFlow<Int> = _repeatMode.asStateFlow()

    private val _shuffleEnabled = MutableStateFlow(false)
    val shuffleEnabled: StateFlow<Boolean> = _shuffleEnabled.asStateFlow()

    init {
        try {
            exoPlayer.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_READY) {
                        _duration.value = exoPlayer.duration.coerceAtLeast(0)
                    }
                    if (playbackState == Player.STATE_ENDED) {
                        playNext()
                    }
                }

                override fun onIsPlayingChanged(playing: Boolean) {
                    _isPlaying.value = playing
                }
            })
        } catch (_: Exception) {
            // Gracefully handle listener registration failure
        }

        viewModelScope.launch {
            while (isActive) {
                try {
                    if (exoPlayer.isPlaying) {
                        _currentPosition.value = exoPlayer.currentPosition.coerceAtLeast(0)
                    }
                } catch (_: Exception) {
                    // Ignore player state read errors
                }
                delay(250)
            }
        }
    }

    fun setPlaylist(items: List<MediaItem>, startIndex: Int = 0) {
        _playlist.value = items
        _currentIndex.value = startIndex
        playItemAt(startIndex)
    }

    fun playItem(item: MediaItem) {
        _currentItem.value = item
        val exoItem = ExoMediaItem.fromUri(item.uri)
        exoPlayer.setMediaItem(exoItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    private fun playItemAt(index: Int) {
        val items = _playlist.value
        if (index in items.indices) {
            _currentIndex.value = index
            playItem(items[index])
        }
    }

    fun playPause() {
        if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
    }

    fun seekTo(position: Long) {
        exoPlayer.seekTo(position)
        _currentPosition.value = position
    }

    fun playNext() {
        val items = _playlist.value
        if (items.isEmpty()) return
        val nextIndex = if (_shuffleEnabled.value) {
            (items.indices).random()
        } else {
            (_currentIndex.value + 1) % items.size
        }
        playItemAt(nextIndex)
    }

    fun playPrevious() {
        val items = _playlist.value
        if (items.isEmpty()) return
        if (exoPlayer.currentPosition > 3000) {
            seekTo(0)
            return
        }
        val prevIndex = if (_shuffleEnabled.value) {
            (items.indices).random()
        } else {
            if (_currentIndex.value > 0) _currentIndex.value - 1 else items.size - 1
        }
        playItemAt(prevIndex)
    }

    fun toggleRepeat() {
        _repeatMode.value = when (_repeatMode.value) {
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ONE
            Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_ALL
            else -> Player.REPEAT_MODE_OFF
        }
        exoPlayer.repeatMode = _repeatMode.value
    }

    fun toggleShuffle() {
        _shuffleEnabled.value = !_shuffleEnabled.value
    }

    fun playVideoUri(uri: Uri) {
        val exoItem = ExoMediaItem.fromUri(uri)
        exoPlayer.setMediaItem(exoItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer.release()
    }
}
