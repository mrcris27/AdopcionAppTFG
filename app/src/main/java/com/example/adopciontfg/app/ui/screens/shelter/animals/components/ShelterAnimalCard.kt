package com.example.adopciontfg.app.ui.screens.shelter.animals.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.adopciontfg.R
import com.example.adopciontfg.data.local.entity.AnimalEntity
import com.example.adopciontfg.data.local.entity.listSubtitle
import com.example.adopciontfg.data.local.entity.statusLabel
import com.example.adopciontfg.model.AnimalStatus
import com.example.adopciontfg.ui.theme.Dimens
import com.example.adopciontfg.ui.theme.elevatedSurface

@Composable
fun ShelterAnimalCard(
    animal: AnimalEntity,
    onClick: () -> Unit,
    onStatusChange: (AnimalStatus) -> Unit,
    modifier: Modifier = Modifier,
) {
    var menuExpanded by remember { mutableStateOf(false) }

    ElevatedCard(
        onClick = onClick,
        modifier = modifier
            .padding(horizontal = Dimens.spacingLg)
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.elevatedSurface(),
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.cardPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(Dimens.thumbnailSize)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Pets,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }

            Spacer(modifier = Modifier.width(Dimens.spacingLg))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = animal.name.orEmpty(),
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(Dimens.spacingXs))
                Text(
                    text = animal.listSubtitle(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(Dimens.spacingXs))
              /*  AssistChip(
                    onClick = {},
                    label = { Text(animal.statusLabel()) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = statusChipColor(animal.status),
                    ),
                )*/
            }

            IconButton(onClick = { menuExpanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.cambiar_estado))
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
            ) {
                AnimalStatus.entries.forEach { status ->
                    DropdownMenuItem(
                        text = { Text(statusMenuLabel(status)) },
                        onClick = {
                            menuExpanded = false
                            onStatusChange(status)
                        },
                    )
                }
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
private fun statusChipColor(status: AnimalStatus?) = when (status) {
    AnimalStatus.AVAILABLE -> MaterialTheme.colorScheme.primaryContainer
    AnimalStatus.RESERVED -> MaterialTheme.colorScheme.tertiaryContainer
    AnimalStatus.ADOPTED -> MaterialTheme.colorScheme.surfaceVariant
    AnimalStatus.UNAVAILABLE -> MaterialTheme.colorScheme.errorContainer
    null -> MaterialTheme.colorScheme.surfaceVariant
}

private fun statusMenuLabel(status: AnimalStatus): String = when (status) {
    AnimalStatus.AVAILABLE -> "Marcar disponible"
    AnimalStatus.RESERVED -> "Marcar reservado"
    AnimalStatus.ADOPTED -> "Marcar adoptado"
    AnimalStatus.UNAVAILABLE -> "Marcar no disponible"
}