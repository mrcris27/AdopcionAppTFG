package com.example.adopciontfg.app.ui.screens.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable

fun SavePhotos() {

    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        // opcional: guardar imagen
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(16.dp)
    ) {

        IconButton(
            onClick = { galleryLauncher.launch("image/*") }
        ) {
            Icon(
                Icons.Default.Photo,
                contentDescription = "Galería",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        IconButton(
            onClick = { cameraLauncher.launch(null) }
        ) {
            Icon(
                Icons.Default.CameraAlt,
                contentDescription = "Cámara",
                tint = MaterialTheme.colorScheme.secondary
            )
        }
    }
}