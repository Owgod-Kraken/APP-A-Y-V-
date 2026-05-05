package com.musicvideoplayer.data.repository

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.musicvideoplayer.data.model.MediaItem
import com.musicvideoplayer.data.model.MediaType
import com.musicvideoplayer.data.model.SortOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaRepository(private val context: Context) {

    suspend fun getMediaItems(
        mediaType: MediaType = MediaType.ALL,
        sortOrder: SortOrder = SortOrder.NAME_ASC,
        searchQuery: String = "",
        favoriteIds: Set<String> = emptySet()
    ): List<MediaItem> = withContext(Dispatchers.IO) {
        val items = mutableListOf<MediaItem>()

        if (mediaType == MediaType.AUDIO || mediaType == MediaType.ALL) {
            items.addAll(queryAudioFiles(favoriteIds))
        }
        if (mediaType == MediaType.VIDEO || mediaType == MediaType.ALL) {
            items.addAll(queryVideoFiles(favoriteIds))
        }

        var filtered = if (searchQuery.isNotBlank()) {
            items.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.artist.contains(searchQuery, ignoreCase = true) ||
                it.album.contains(searchQuery, ignoreCase = true)
            }
        } else items

        filtered = when (sortOrder) {
            SortOrder.NAME_ASC -> filtered.sortedBy { it.title.lowercase() }
            SortOrder.NAME_DESC -> filtered.sortedByDescending { it.title.lowercase() }
            SortOrder.DURATION_ASC -> filtered.sortedBy { it.duration }
            SortOrder.DURATION_DESC -> filtered.sortedByDescending { it.duration }
            SortOrder.DATE_ASC -> filtered.sortedBy { it.dateAdded }
            SortOrder.DATE_DESC -> filtered.sortedByDescending { it.dateAdded }
            SortOrder.SIZE_ASC -> filtered.sortedBy { it.size }
            SortOrder.SIZE_DESC -> filtered.sortedByDescending { it.size }
        }

        filtered
    }

    private fun queryAudioFiles(favoriteIds: Set<String>): List<MediaItem> {
        val items = mutableListOf<MediaItem>()
        try {
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.ALBUM_ID
        )

        val selection = "${MediaStore.Audio.Media.DURATION} > ?"
        val selectionArgs = arrayOf("5000") // Filter out short audio clips

        context.contentResolver.query(
            collection, projection, selection, selectionArgs,
            "${MediaStore.Audio.Media.TITLE} ASC"
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            val dataCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val dateCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
            val mimeCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)
            val albumIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val albumId = cursor.getLong(albumIdCol)
                val albumArtUri = ContentUris.withAppendedId(
                    Uri.parse("content://media/external/audio/albumart"), albumId
                )
                items.add(
                    MediaItem(
                        id = id,
                        title = cursor.getString(titleCol) ?: "Desconocido",
                        artist = cursor.getString(artistCol) ?: "Artista desconocido",
                        album = cursor.getString(albumCol) ?: "Álbum desconocido",
                        duration = cursor.getLong(durationCol),
                        size = cursor.getLong(sizeCol),
                        path = cursor.getString(dataCol) ?: "",
                        uri = ContentUris.withAppendedId(collection, id),
                        dateAdded = cursor.getLong(dateCol),
                        mimeType = cursor.getString(mimeCol) ?: "audio/*",
                        albumArtUri = albumArtUri,
                        isFavorite = favoriteIds.contains(id.toString())
                    )
                )
            }
        }
        } catch (_: SecurityException) {
            // Permissions not yet granted
        } catch (_: Exception) {
            // Handle any other query errors gracefully
        }
        return items
    }

    private fun queryVideoFiles(favoriteIds: Set<String>): List<MediaItem> {
        val items = mutableListOf<MediaItem>()
        try {
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.ARTIST,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.MIME_TYPE
        )

        context.contentResolver.query(
            collection, projection, null, null,
            "${MediaStore.Video.Media.TITLE} ASC"
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)
            val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST)
            val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val dataCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            val dateCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
            val mimeCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                items.add(
                    MediaItem(
                        id = id,
                        title = cursor.getString(titleCol) ?: "Desconocido",
                        artist = cursor.getString(artistCol) ?: "",
                        album = "",
                        duration = cursor.getLong(durationCol),
                        size = cursor.getLong(sizeCol),
                        path = cursor.getString(dataCol) ?: "",
                        uri = ContentUris.withAppendedId(collection, id),
                        dateAdded = cursor.getLong(dateCol),
                        mimeType = cursor.getString(mimeCol) ?: "video/*",
                        isFavorite = favoriteIds.contains(id.toString())
                    )
                )
            }
        }
        } catch (_: SecurityException) {
            // Permissions not yet granted
        } catch (_: Exception) {
            // Handle any other query errors gracefully
        }
        return items
    }
}
