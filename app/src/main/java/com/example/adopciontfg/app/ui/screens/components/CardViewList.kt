package com.example.adopciontfg.app.ui.screens.components

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.adopciontfg.R
import com.example.adopciontfg.ui.theme.Dimens
import com.example.adopciontfg.ui.theme.elevatedSurface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun CardViewList(
    name: String,
    onClick: () -> Unit,
    subtitle: String = stringResource(R.string.tap_to_view_more_information),
    photoUri: String? = null,
    placeholderIcon: ImageVector = Icons.Default.Pets,
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .padding(horizontal = Dimens.spacingSm)
            .fillMaxWidth()
            .wrapContentHeight()
            .animateContentSize(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.elevatedSurface(),
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 6.dp,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.cardPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AnimalListThumbnail(
                photoUri = photoUri,
                placeholderIcon = placeholderIcon,
            )

            Spacer(modifier = Modifier.width(Dimens.spacingLg))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(Dimens.spacingXs))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun AnimalListThumbnail(
    photoUri: String?,
    modifier: Modifier = Modifier,
    placeholderIcon: ImageVector = Icons.Default.Pets,
) {
    UriThumbnail(
        photoUri = photoUri,
        placeholderIcon = placeholderIcon,
        modifier = modifier,
        size = Dimens.thumbnailSize,
        iconSize = 36.dp,
        shape = MaterialTheme.shapes.medium,
    )
}

@Composable
fun UriThumbnail(
    photoUri: String?,
    placeholderIcon: ImageVector,
    modifier: Modifier = Modifier,
    size: Dp = Dimens.thumbnailSize,
    iconSize: Dp = 36.dp,
    shape: Shape = MaterialTheme.shapes.medium,
) {
    val imageBitmap by rememberImageBitmap(photoUri.orEmpty())

    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        if (imageBitmap != null) {
            Image(
                bitmap = imageBitmap!!,
                contentDescription = null,
                modifier = Modifier
                    .size(size)
                    .clip(shape),
                contentScale = ContentScale.Crop,
            )
        } else {
            Icon(
                imageVector = placeholderIcon,
                contentDescription = null,
                modifier = Modifier.size(iconSize),
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun rememberImageBitmap(uriString: String): State<ImageBitmap?> {
    val context = LocalContext.current
    return androidx.compose.runtime.produceState<ImageBitmap?>(null, uriString, context) {
        value = withContext(Dispatchers.IO) {
            if (uriString.isBlank()) {
                null
            } else {
                runCatching {
                    context.contentResolver.openInputStream(Uri.parse(uriString))?.use { stream ->
                        BitmapFactory.decodeStream(stream)?.asImageBitmap()
                    }
                }.getOrNull()
            }
        }
    }
}
