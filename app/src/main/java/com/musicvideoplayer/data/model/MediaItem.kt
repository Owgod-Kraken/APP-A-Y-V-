package com.musicvideoplayer.data.model

import android.net.Uri

data class MediaItem(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val size: Long,
    val path: String,
    val uri: Uri,
    val dateAdded: Long,
    val mimeType: String,
    val albumArtUri: Uri? = null,
    val isFavorite: Boolean = false
) {
    val isAudio: Boolean
        get() = mimeType.startsWith("audio/")

    val isVideo: Boolean
        get() = mimeType.startsWith("video/")

    val formattedDuration: String
        get() {
            val totalSeconds = duration / 1000
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60
            return if (hours > 0) {
                String.format("%d:%02d:%02d", hours, minutes, seconds)
            } else {
                String.format("%d:%02d", minutes, seconds)
            }
        }

    val formattedSize: String
        get() {
            val kb = size / 1024.0
            val mb = kb / 1024.0
            val gb = mb / 1024.0
            return when {
                gb >= 1 -> String.format("%.1f GB", gb)
                mb >= 1 -> String.format("%.1f MB", mb)
                else -> String.format("%.1f KB", kb)
            }
        }
}

enum class SortOrder {
    NAME_ASC,
    NAME_DESC,
    DURATION_ASC,
    DURATION_DESC,
    DATE_ASC,
    DATE_DESC,
    SIZE_ASC,
    SIZE_DESC
}

enum class MediaType {
    AUDIO,
    VIDEO,
    ALL
}
