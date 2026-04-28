package com.musicvideoplayer.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
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
import com.musicvideoplayer.data.model.SortOrder
import com.musicvideoplayer.ui.components.MediaItemCard
import com.musicvideoplayer.ui.components.FileInfoDialog
import com.musicvideoplayer.viewmodel.MediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    mediaViewModel: MediaViewModel,
    onAudioClick: (MediaItem, List<MediaItem>) -> Unit,
    onVideoClick: (MediaItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val audioFiles by mediaViewModel.audioFiles.collectAsState()
    val videoFiles by mediaViewModel.videoFiles.collectAsState()
    val isLoading by mediaViewModel.isLoading.collectAsState()
    val searchQuery by mediaViewModel.searchQuery.collectAsState()
    val sortOrder by mediaViewModel.sortOrder.collectAsState()
    val showFavoritesOnly by mediaViewModel.showFavoritesOnly.collectAsState()

    var isSearchActive by remember { mutableStateOf(false) }
    var showSortOptions by remember { mutableStateOf(false) }
    var infoItem by remember { mutableStateOf<MediaItem?>(null) }

    Column(modifier = modifier.fillMaxSize()) {
        // Search bar
        SearchBar(
            query = searchQuery,
            onQueryChange = { mediaViewModel.updateSearchQuery(it) },
            onSearch = { isSearchActive = false },
            active = isSearchActive,
            onActiveChange = { isSearchActive = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = { Text("Buscar música o videos...") },
            leadingIcon = {
                Icon(Icons.Filled.Search, contentDescription = "Buscar")
            },
            trailingIcon = {
                Row {
                    IconButton(onClick = { mediaViewModel.toggleFavoritesOnly() }) {
                        Icon(
                            imageVector = if (showFavoritesOnly) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favoritos",
                            tint = if (showFavoritesOnly) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { showSortOptions = !showSortOptions }) {
                        Icon(Icons.Filled.FilterList, contentDescription = "Ordenar")
                    }
                }
            }
        ) {
            // Search suggestions could go here
        }

        // Sort chips
        AnimatedVisibility(
            visible = showSortOptions,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LazyRow(
                modifier = Modifier.padding(vertical = 8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val sortOptions = listOf(
                    "Nombre ↑" to SortOrder.NAME_ASC,
                    "Nombre ↓" to SortOrder.NAME_DESC,
                    "Duración ↑" to SortOrder.DURATION_ASC,
                    "Duración ↓" to SortOrder.DURATION_DESC,
                    "Fecha ↑" to SortOrder.DATE_ASC,
                    "Fecha ↓" to SortOrder.DATE_DESC,
                    "Tamaño ↑" to SortOrder.SIZE_ASC,
                    "Tamaño ↓" to SortOrder.SIZE_DESC
                )
                items(sortOptions) { (label, order) ->
                    FilterChip(
                        selected = sortOrder == order,
                        onClick = { mediaViewModel.updateSortOrder(order) },
                        label = { Text(label) }
                    )
                }
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Stats cards
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            icon = Icons.Filled.AudioFile,
                            count = audioFiles.size,
                            label = "Canciones",
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            icon = Icons.Filled.VideoFile,
                            count = videoFiles.size,
                            label = "Videos",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Audio section
                if (audioFiles.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Música",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    items(audioFiles.take(5), key = { "audio_${it.id}" }) { item ->
                        MediaItemCard(
                            item = item,
                            onClick = { onAudioClick(item, audioFiles) },
                            onFavoriteClick = { mediaViewModel.toggleFavorite(item) },
                            onInfoClick = { infoItem = item }
                        )
                    }
                    if (audioFiles.size > 5) {
                        item {
                            Text(
                                text = "Ver todas las ${audioFiles.size} canciones →",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }

                // Video section
                if (videoFiles.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Videos",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    items(videoFiles.take(5), key = { "video_${it.id}" }) { item ->
                        MediaItemCard(
                            item = item,
                            onClick = { onVideoClick(item) },
                            onFavoriteClick = { mediaViewModel.toggleFavorite(item) },
                            onInfoClick = { infoItem = item }
                        )
                    }
                    if (videoFiles.size > 5) {
                        item {
                            Text(
                                text = "Ver todos los ${videoFiles.size} videos →",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }

                // Empty state
                if (audioFiles.isEmpty() && videoFiles.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "No se encontraron archivos",
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text(
                                    text = "Asegúrate de tener archivos multimedia en tu dispositivo",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Bottom padding for mini player
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }

    infoItem?.let { item ->
        FileInfoDialog(item = item, onDismiss = { infoItem = null })
    }
}

@Composable
private fun StatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    count: Int,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Column {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}
