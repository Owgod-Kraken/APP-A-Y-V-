# MediaPlayer Pro - Reproductor de Audio y Video

Aplicación completa de reproductor multimedia para Android, desarrollada con Kotlin y Jetpack Compose.

## Características

### Funcionalidad Principal
- Escaneo automático de archivos multimedia del dispositivo
- Listas separadas para Música y Videos
- Reproducción, pausa, adelantar y retroceder
- Muestra duración, nombre del archivo y miniaturas
- Soporte para múltiples formatos: MP3, WAV, MP4, MKV, AVI, FLAC, etc.

### Reproductor de Audio
- Barra de progreso interactiva
- Botones de play/pause, siguiente, anterior
- Muestra portada del álbum (si existe metadata)
- Modo aleatorio y repetición
- Animación de ecualizador en tiempo real
- Mini reproductor persistente en la parte inferior

### Reproductor de Video
- Basado en ExoPlayer (Media3)
- Modo pantalla completa
- Controles personalizados con overlay
- Adelantar/retroceder 10 segundos

### Interfaz (UI/UX)
- Material Design 3
- Interfaz moderna tipo Spotify/VLC
- Jetpack Compose
- Animaciones suaves (transiciones, ecualizador, efectos en botones)
- Tema claro y oscuro automático

### Personalización
- 8 temas de colores predefinidos (Azul, Rojo, Verde, Morado, Naranja, Turquesa, Rosa, Amarillo)
- Modo claro, oscuro o automático (sistema)
- Preferencias guardadas con DataStore

### Extras
- Favoritos (marcar canciones/videos)
- Búsqueda de archivos
- Ordenar por nombre, duración, fecha o tamaño
- Información detallada del archivo

## Arquitectura

- **MVVM** (Model-View-ViewModel)
- Capas separadas: UI → ViewModel → Repository
- StateFlow para estado reactivo

## Estructura del Proyecto

```
app/src/main/java/com/musicvideoplayer/
├── MainActivity.kt              # Activity principal y navegación
├── data/
│   ├── model/
│   │   ├── MediaItem.kt         # Modelo de datos multimedia
│   │   └── AppTheme.kt          # Modelos de tema
│   ├── preferences/
│   │   └── AppPreferences.kt    # DataStore para preferencias
│   └── repository/
│       └── MediaRepository.kt   # Acceso a MediaStore
├── viewmodel/
│   ├── MediaViewModel.kt        # ViewModel para listas de medios
│   ├── PlayerViewModel.kt       # ViewModel del reproductor
│   └── SettingsViewModel.kt     # ViewModel de ajustes
└── ui/
    ├── theme/
    │   ├── Color.kt             # Paletas de colores
    │   ├── Theme.kt             # Sistema de temas Material 3
    │   └── Type.kt              # Tipografía
    ├── navigation/
    │   └── Navigation.kt        # Rutas y navegación
    ├── components/
    │   ├── MediaItemCard.kt     # Card de elemento multimedia
    │   ├── EqualizerAnimation.kt # Animación de ecualizador
    │   └── MiniPlayer.kt        # Mini reproductor
    └── screens/
        ├── home/
        │   ├── HomeScreen.kt    # Pantalla principal
        │   ├── MusicListScreen.kt # Lista de música
        │   └── VideoListScreen.kt # Lista de videos
        ├── player/
        │   └── AudioPlayerScreen.kt # Reproductor de audio
        ├── videoplayer/
        │   └── VideoPlayerScreen.kt # Reproductor de video
        └── settings/
            └── SettingsScreen.kt # Pantalla de ajustes
```

## Cómo Ejecutar

### Requisitos
- Android Studio Hedgehog (2023.1.1) o superior
- JDK 17
- Android SDK 34
- Dispositivo o emulador con Android 8.0 (API 26) o superior

### Pasos
1. Clona el repositorio:
   ```bash
   git clone https://github.com/Owgod-Kraken/APP-A-Y-V.git
   ```
2. Abre el proyecto en Android Studio
3. Espera a que Gradle sincronice las dependencias
4. Conecta un dispositivo Android o inicia un emulador
5. Haz clic en "Run" (▶)

### Permisos
La app solicitará permisos de almacenamiento automáticamente:
- **Android 13+**: `READ_MEDIA_AUDIO` y `READ_MEDIA_VIDEO`
- **Android 12 e inferior**: `READ_EXTERNAL_STORAGE`

## Librerías Utilizadas
- **ExoPlayer (Media3)** - Reproducción de audio/video
- **Coil** - Carga de imágenes y miniaturas
- **Material 3** - Diseño de interfaz
- **Jetpack Compose** - UI declarativa
- **Navigation Compose** - Navegación entre pantallas
- **DataStore** - Almacenamiento de preferencias
- **Accompanist** - Utilidades para permisos

## Licencia
MIT License
