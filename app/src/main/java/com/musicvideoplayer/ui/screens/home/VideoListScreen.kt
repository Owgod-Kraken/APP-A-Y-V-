package com.musicvideoplayer.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.musicvideoplayer.data.model.MediaItem
import com.musicvideoplayer.ui.components.FileInfoDialog
import com.musicvideoplayer.ui.components.MediaItemCard
import com.musicvideoplayer.viewmodel.MediaViewModel

@Composable
fun VideoListScreen(
    mediaViewModel: MediaViewModel,
    onVideoClick: (MediaItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val videoFiles by mediaViewModel.videoFiles.collectAsState()
    val isLoading by mediaViewModel.isLoading.collectAsState()
    var infoItem by remember { mutableStateOf<MediaItem?>(null) }

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "Todos los Videos",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (videoFiles.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No se encontraron videos",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = "${videoFiles.size} videos",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                items(videoFiles, key = { "video_${it.id}" }) { item ->
                    MediaItemCard(
                        item = item,
                        onClick = { onVideoClick(item) },
                        onFavoriteClick = { mediaViewModel.toggleFavorite(item) },
                        onInfoClick = { infoItem = item }
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }

    infoItem?.let { item ->
        FileInfoDialog(item = item, onDismiss = { infoItem = null })
    }
}
