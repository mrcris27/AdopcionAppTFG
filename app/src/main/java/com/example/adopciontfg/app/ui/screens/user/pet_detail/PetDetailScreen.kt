package com.example.adopciontfg.app.ui.screens.user.pet_detail

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.adopciontfg.R
import com.example.adopciontfg.app.ui.components.skeleton.PetDetailBottomBarSkeleton
import com.example.adopciontfg.app.ui.components.skeleton.PetDetailContentSkeleton
import com.example.adopciontfg.app.ui.components.skeleton.PetDetailTopBarTitleSkeleton
import com.example.adopciontfg.app.ui.screens.components.AppOutlinedButton
import com.example.adopciontfg.app.ui.screens.components.AppSecondaryButton
import com.example.adopciontfg.app.ui.screens.components.AppSectionTitle
import com.example.adopciontfg.app.ui.screens.components.characteristicLabel
import com.example.adopciontfg.app.ui.screens.components.speciesLabel
import com.example.adopciontfg.data.local.entity.AnimalEntity
import com.example.adopciontfg.data.local.entity.ageInYears
import com.example.adopciontfg.data.local.entity.displayPhotos
import com.example.adopciontfg.model.Characteristic
import com.example.adopciontfg.model.Species
import com.example.adopciontfg.ui.theme.AdoptionTheme
import com.example.adopciontfg.ui.theme.Dimens
import com.example.adopciontfg.ui.theme.elevatedSurface
import com.example.adopciontfg.ui.theme.subtleDivider
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetDetailScreen(
    animal: AnimalEntity?,
    onBackClick: () -> Unit,
    onAdoptClick: () -> Unit,
    isLoading: Boolean = false,
) {
    var showDonationDialog by remember { mutableStateOf(false) }
    var donationAmount by remember { mutableFloatStateOf(20f) }
    var isDonationConfirmed by remember { mutableStateOf(false) }
    val animalName = animal?.name.orEmpty()

    if (showDonationDialog && animalName.isNotBlank()) {
        DonationDialog(
            animalName = animalName,
            donationAmount = donationAmount.roundToInt(),
            isDonationConfirmed = isDonationConfirmed,
            onAmountChange = {
                donationAmount = it
                isDonationConfirmed = false
            },
            onConfirmClick = { isDonationConfirmed = true },
            onDismissRequest = {
                showDonationDialog = false
                isDonationConfirmed = false
            }
        )
    }

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
                            contentDescription = stringResource(R.string.volver),
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
                            text = stringResource(R.string.adoptar_ahora),
                            onClick = onAdoptClick,
                            modifier = Modifier.weight(1f)
                        )
                        AppOutlinedButton(
                            text = stringResource(R.string.donar),
                            onClick = { showDonationDialog = true },
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
                            val animalSpecies = animal.species
                            InfoRow(stringResource(R.string.nombre), animal.name.orEmpty())
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.subtleDivider()
                            )
                            InfoRow(
                                stringResource(R.string.edad),
                                stringResource(R.string.animal_age_years, animal.ageInYears())
                            )
                            InfoRow(
                                stringResource(R.string.especie),
                                if (animalSpecies != null) {
                                    speciesLabel(animalSpecies)
                                } else {
                                    ""
                                }
                            )
                            InfoRow(
                                stringResource(R.string.genero),
                                if (animal.isSex) {
                                    stringResource(R.string.hembra)
                                } else {
                                    stringResource(R.string.macho)
                                }
                            )
                            if (!animal.characteristics.isNullOrEmpty()) {
                                val characteristicLabels = mutableListOf<String>()
                                for (characteristic in animal.characteristics) {
                                    characteristicLabels += characteristicLabel(characteristic)
                                }
                                InfoRow(
                                    stringResource(R.string.caracteristicas),
                                    characteristicLabels.joinToString(", ")
                                )
                            }
                            Spacer(modifier = Modifier.height(Dimens.spacingXs))
                            AppSectionTitle(text = stringResource(R.string.sobre_esta_mascota))
                            Text(
                                text = animal.description?.takeIf { it.isNotBlank() }
                                    ?: stringResource(R.string.mascota_descripcion_default),
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
private fun DonationDialog(
    animalName: String,
    donationAmount: Int,
    isDonationConfirmed: Boolean,
    onAmountChange: (Float) -> Unit,
    onConfirmClick: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.donation_dialog_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)) {
                Text(
                    text = stringResource(R.string.donation_dialog_pet_name, animalName),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = stringResource(R.string.donation_dialog_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.donation_amount_selected, donationAmount),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Slider(
                    value = donationAmount.toFloat(),
                    onValueChange = onAmountChange,
                    valueRange = 5f..100f,
                    steps = 18
                )
                if (isDonationConfirmed) {
                    Text(
                        text = stringResource(R.string.donation_confirmation_message, animalName),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = if (isDonationConfirmed) {
                    onDismissRequest
                } else {
                    onConfirmClick
                }
            ) {
                Text(
                    if (isDonationConfirmed) {
                        stringResource(R.string.cerrar)
                    } else {
                        stringResource(R.string.confirmar_donativo)
                    }
                )
            }
        },
        dismissButton = {
            if (!isDonationConfirmed) {
                TextButton(onClick = onDismissRequest) {
                    Text(stringResource(R.string.cancelar))
                }
            }
        }
    )
}

@Composable
private fun PetPhotosCarousel(photos: List<String>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
    ) {
        Text(
            text = stringResource(R.string.fotos),
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
                    text = stringResource(R.string.sin_fotos_disponibles),
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
@Preview(showBackground = true)
@Composable
fun PetDetailPreview() {
    AdoptionTheme {
        PetDetailScreen(
            animal = previewAnimal(),
            onBackClick = {},
            onAdoptClick = {},
        )
    }
}

private fun previewAnimal(): AnimalEntity = AnimalEntity(
    "preview-animal-1",
    "Luna",
    true,
    null,
    emptyList(),
    0L,
    "Busca una familia tranquila.",
    Species.PERRO,
    listOf(Characteristic.TRANQUILO),
    "preview-shelter",
    true,
)