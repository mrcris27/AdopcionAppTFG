package com.example.adopciontfg.app.ui.screens.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.adopciontfg.R
import com.example.adopciontfg.ui.theme.Dimens
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SavePhotos(
    modifier: Modifier = Modifier,
    uris: List<Uri>? = null,
    onUrisChange: ((List<Uri>) -> Unit)? = null,
    maxPhotos: Int = Int.MAX_VALUE,
) {
    val context = LocalContext.current
    var internalUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var cameraUri by remember { mutableStateOf<Uri?>(null) }
    val currentUris = uris ?: internalUris
    val updateUris: (List<Uri>) -> Unit = { newUris ->
        if (onUrisChange != null) {
            onUrisChange(newUris)
        } else {
            internalUris = newUris
        }
    }

    val singleSelection = maxPhotos == 1
    val canAddMore = singleSelection || currentUris.size < maxPhotos
    val appendUri: (Uri) -> Unit = { uri ->
        val updatedUris = if (singleSelection) {
            listOf(uri)
        } else {
            (currentUris + uri).distinct().take(maxPhotos)
        }
        updateUris(updatedUris)
    }

    val singleGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) appendUri(uri)
    }

    val multipleGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { picked ->
        if (picked.isNotEmpty()) {
            updateUris((currentUris + picked).distinct().take(maxPhotos))
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        val capturedUri = cameraUri
        if (success && capturedUri != null) {
            appendUri(capturedUri)
        }
    }

    fun launchCamera() {
        val uri = createAnimalImageUri(context)
        cameraUri = uri
        cameraLauncher.launch(uri)
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) launchCamera()
    }

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingMd),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledIconButton(
                    onClick = {
                        if (singleSelection) {
                            singleGalleryLauncher.launch("image/*")
                        } else {
                            multipleGalleryLauncher.launch("image/*")
                        }
                    },
                    enabled = canAddMore,
                    modifier = Modifier.size(48.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Photo,
                        contentDescription = stringResource(R.string.galeria)
                    )
                }
                FilledIconButton(
                    onClick = {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            launchCamera()
                        } else {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    enabled = canAddMore,
                    modifier = Modifier.size(48.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = stringResource(R.string.camara)
                    )
                }
            }
            if (currentUris.isNotEmpty()) {
                val label = if (currentUris.size == 1) {
                    stringResource(R.string.foto_anadida)
                } else {
                    stringResource(R.string.fotos_anadidas, currentUris.size)
                }
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = stringResource(R.string.toca_icono_anadir_fotos),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            PhotoGrid(
                uris = currentUris,
                onRemove = { uri ->
                    updateUris(currentUris.filterNot { it == uri })
                }
            )
        }
    }
}

@Composable
private fun PhotoGrid(
    uris: List<Uri>,
    onRemove: (Uri) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (uris.isEmpty()) return

    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
        maxItemsInEachRow = 3,
    ) {
        uris.forEach { uri ->
            PhotoThumbnail(
                uri = uri,
                onRemove = { onRemove(uri) },
            )
        }
    }
}

@Composable
private fun PhotoThumbnail(
    uri: Uri,
    onRemove: () -> Unit,
) {
    val imageBitmap by rememberImageBitmap(uri)

    Box(
        modifier = Modifier
            .size(92.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface),
    ) {
        if (imageBitmap != null) {
            Image(
                bitmap = imageBitmap!!,
                contentDescription = null,
                modifier = Modifier
                    .size(92.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop,
            )
        } else {
            Icon(
                imageVector = Icons.Default.Photo,
                contentDescription = null,
                modifier = Modifier.align(Alignment.Center),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(2.dp)
                .size(28.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.56f)),
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.quitar_foto),
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Composable
private fun rememberImageBitmap(uri: Uri): State<ImageBitmap?> {
    val context = LocalContext.current
    return produceState<ImageBitmap?>(null, uri, context) {
        value = withContext(Dispatchers.IO) {
            runCatching {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    BitmapFactory.decodeStream(stream)?.asImageBitmap()
                }
            }.getOrNull()
        }
    }
}

private fun createAnimalImageUri(context: Context): Uri {
    val imageDir = File(context.cacheDir, "animal_images").apply { mkdirs() }
    val imageFile = File.createTempFile("animal_", ".jpg", imageDir)
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imageFile,
    )
}

@Preview
@Composable
fun SavePhotosPreview() {
    SavePhotos(modifier = Modifier.padding(16.dp))
}
