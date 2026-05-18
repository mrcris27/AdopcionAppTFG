package com.example.adopciontfg.app.ui.screens.pet_detail_screen

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.adopciontfg.app.ui.components.skeleton.PetDetailBottomBarSkeleton
import com.example.adopciontfg.app.ui.components.skeleton.PetDetailContentSkeleton
import com.example.adopciontfg.app.ui.components.skeleton.PetDetailTopBarTitleSkeleton
import com.example.adopciontfg.app.ui.screens.components.AppOutlinedButton
import com.example.adopciontfg.app.ui.screens.components.AppSecondaryButton
import com.example.adopciontfg.app.ui.screens.components.AppSectionTitle
import com.example.adopciontfg.data.local.entity.AnimalEntity
import com.example.adopciontfg.data.local.entity.ageInYears
import com.example.adopciontfg.data.local.entity.displayPhotos
import com.example.adopciontfg.data.local.entity.formattedCharacteristics
import com.example.adopciontfg.data.local.entity.genderLabel
import com.example.adopciontfg.data.sampleAnimals
import com.example.adopciontfg.ui.theme.AdoptionTheme
import com.example.adopciontfg.ui.theme.Dimens
import com.example.adopciontfg.ui.theme.elevatedSurface
import com.example.adopciontfg.ui.theme.subtleDivider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetDetailScreen(
    animal: AnimalEntity?,
    onBackClick: () -> Unit,
    onAdoptClick: () -> Unit,
    isLoading: Boolean = false,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    if (isLoading) {
                        PetDetailTopBarTitleSkeleton()
                    } else {
                        Text(
                            text = animal?.name.orEmpty(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.elevatedSurface()
                )
            )
        },
        bottomBar = {
            if (isLoading) {
                PetDetailBottomBarSkeleton()
            } else {
                Surface(
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.elevatedSurface(),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimens.cardPadding),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
                    ) {
                        AppSecondaryButton(
                            text = "Adoptar ahora",
                            onClick = onAdoptClick,
                            modifier = Modifier.weight(1f)
                        )
                        AppOutlinedButton(
                            text = "Donar",
                            onClick = { },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    ) { padding ->
        if (isLoading || animal == null) {
            PetDetailContentSkeleton(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            )
        } else {
            val photos = animal.displayPhotos()
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg),
                contentPadding = PaddingValues(Dimens.screenPadding)
            ) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.elevatedSurface()
                        ),
                        shape = MaterialTheme.shapes.large,
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(Dimens.cardPadding),
                            verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
                        ) {
                            InfoRow("Nombre", animal.name.orEmpty())
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.subtleDivider()
                            )
                            InfoRow("Edad", "${animal.ageInYears()} años")
                            InfoRow("Especie", animal.species?.name.orEmpty())
                            InfoRow("Género", animal.genderLabel())
                            if (!animal.characteristics.isNullOrEmpty()) {
                                InfoRow("Características", animal.formattedCharacteristics())
                            }
                            Spacer(modifier = Modifier.height(Dimens.spacingXs))
                            AppSectionTitle(text = "Sobre esta mascota")
                            Text(
                                text = animal.description?.takeIf { it.isNotBlank() }
                                    ?: "Esta mascota busca un hogar lleno de amor. " +
                                    "Está esperando a alguien especial que le dé una segunda oportunidad 🐾",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                item {
                    PetPhotosCarousel(photos)
                }
            }
        }
    }
}

@Composable
private fun PetPhotosCarousel(photos: List<String>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
    ) {
        Text(
            text = "Fotos",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        if (photos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(MaterialTheme.shapes.large)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Sin fotos disponibles",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
                contentPadding = PaddingValues(vertical = Dimens.spacingXs)
            ) {
                itemsIndexed(photos, key = { index, _ -> index }) { _, _ ->
                    Box(
                        modifier = Modifier
                            .width(280.dp)
                            .height(200.dp)
                            .clip(MaterialTheme.shapes.large)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🐶",
                            style = MaterialTheme.typography.displaySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true, name = "Cargando")
@Composable
fun PetDetailLoadingPreview() {
    AdoptionTheme {
        PetDetailScreen(
            animal = null,
            onBackClick = {},
            onAdoptClick = {},
            isLoading = true,
        )
    }
}

@Preview
@Composable
fun PetDetailPreview() {
    AdoptionTheme {
        PetDetailScreen(
            animal = sampleAnimals.first(),
            onBackClick = {},
            onAdoptClick = {},
        )
    }
}
