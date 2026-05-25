package com.example.adopciontfg.app.ui.screens.user.shelter_profile

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.adopciontfg.R
import com.example.adopciontfg.app.ui.components.skeleton.ShelterCardListSkeleton
import com.example.adopciontfg.app.ui.screens.components.AppSectionTitle
import com.example.adopciontfg.app.ui.screens.components.CardViewList
import com.example.adopciontfg.app.ui.screens.components.DataRefreshErrorDialog
import com.example.adopciontfg.app.ui.screens.components.ListCardView
import com.example.adopciontfg.app.ui.screens.components.SearchSection
import com.example.adopciontfg.app.ui.screens.components.UriThumbnail
import com.example.adopciontfg.app.ui.screens.components.animalListSubtitle
import com.example.adopciontfg.app.ui.screens.components.characteristicLabel
import com.example.adopciontfg.app.ui.screens.components.speciesLabel
import com.example.adopciontfg.app.ui.state.DataRefreshError
import com.example.adopciontfg.data.local.entity.AnimalEntity
import com.example.adopciontfg.data.local.entity.ShelterEntity
import com.example.adopciontfg.data.local.entity.listSubtitle
import com.example.adopciontfg.model.Characteristic
import com.example.adopciontfg.model.Species
import com.example.adopciontfg.ui.theme.AdoptionTheme
import com.example.adopciontfg.ui.theme.Dimens
import com.example.adopciontfg.ui.theme.elevatedSurface
import com.example.adopciontfg.ui.theme.subtleDivider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelterProfileScreen(
    shelter: ShelterEntity,
    animals: List<AnimalEntity>,
    hasAnimals: Boolean = animals.isNotEmpty(),
    query: String = "",
    selectedSpecies: Species? = null,
    selectedSex: Boolean? = null,
    selectedCharacteristics: Set<Characteristic> = emptySet(),
    hasActiveFilters: Boolean = false,
    isFiltering: Boolean = query.isNotBlank() || hasActiveFilters,
    onBackClick: () -> Unit,
    onPetClick: (String) -> Unit,
    onQueryChange: (String) -> Unit = {},
    onSpeciesFilterChange: (Species?) -> Unit = {},
    onSexFilterChange: (Boolean?) -> Unit = {},
    onCharacteristicToggle: (Characteristic) -> Unit = {},
    onClearFilters: () -> Unit = {},
    isPetsLoading: Boolean = false,
    isRefreshing: Boolean = false,
    refreshError: DataRefreshError? = null,
    onRefresh: () -> Unit = {},
    onRefreshErrorDismiss: () -> Unit = {},
) {
    var shelterCardExpanded by remember { mutableStateOf(false) }
    var showFilters by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val filterSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val chevronRotation by animateFloatAsState(
        targetValue = if (shelterCardExpanded) 180f else 0f,
        label = "shelterCardChevron",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
    ) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                ElevatedCard(
                    onClick = { shelterCardExpanded = !shelterCardExpanded },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.spacingSm, vertical = Dimens.spacingSm)
                        .animateContentSize(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.elevatedSurface()
                    ),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimens.cardPadding)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = onBackClick) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.volver),
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            }

                            Spacer(modifier = Modifier.width(Dimens.spacingSm))

                            UriThumbnail(
                                photoUri = shelter.profilePicture,
                                placeholderIcon = Icons.Default.AccountCircle,
                                shape = CircleShape,
                                size = Dimens.avatarSize,
                                iconSize = 44.dp,
                            )

                            Spacer(modifier = Modifier.width(Dimens.spacingMd))

                            Text(
                                text = shelter.name.orEmpty(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f)
                            )

                            Icon(
                                imageVector = Icons.Default.ExpandMore,
                                contentDescription = if (shelterCardExpanded) {
                                    stringResource(R.string.ocultar_info_protectora)
                                } else {
                                    stringResource(R.string.mostrar_info_protectora)
                                },
                                modifier = Modifier.rotate(chevronRotation),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (shelterCardExpanded) {
                            Spacer(modifier = Modifier.height(Dimens.spacingMd))
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.subtleDivider()
                            )
                            Spacer(modifier = Modifier.height(Dimens.spacingMd))

                            val hasContactDetails = shelter.address.orEmpty().isNotBlank() ||
                                shelter.cif.orEmpty().isNotBlank() ||
                                shelter.email.orEmpty().isNotBlank() ||
                                shelter.phone.orEmpty().isNotBlank()

                            if (!hasContactDetails) {
                                Text(
                                    text = stringResource(R.string.shelter_info_empty),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                if (shelter.address.orEmpty().isNotBlank()) {
                                    Text(
                                        text = shelter.address.orEmpty(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (shelter.cif.orEmpty().isNotBlank()) {
                                    Spacer(modifier = Modifier.height(Dimens.spacingSm))
                                    Text(
                                        text = stringResource(R.string.cif_format, shelter.cif.orEmpty()),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (shelter.email.orEmpty().isNotBlank()) {
                                    Spacer(modifier = Modifier.height(Dimens.spacingSm))
                                    ContactActionText(
                                        text = shelter.email.orEmpty(),
                                        onClick = { openEmailApp(context, shelter.email.orEmpty()) },
                                    )
                                }
                                if (shelter.phone.orEmpty().isNotBlank()) {
                                    Spacer(modifier = Modifier.height(Dimens.spacingSm))
                                    ContactActionText(
                                        text = shelter.phone.orEmpty(),
                                        onClick = { openPhoneApp(context, shelter.phone.orEmpty()) },
                                    )
                                }
                            }
                        }
                    }
                }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = Dimens.spacingSm,
                        end = Dimens.spacingXs,
                        top = Dimens.spacingSm,
                        bottom = Dimens.spacingXs
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppSectionTitle(
                    text = stringResource(R.string.animales_disponibles),
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { showFilters = true },
                    enabled = !isPetsLoading && hasAnimals,
                ) {
                    BadgedBox(
                        badge = {
                            if (hasActiveFilters) {
                                Badge()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = stringResource(R.string.abrir_filtros_animales),
                            tint = if (hasActiveFilters) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
            }

            if (!isPetsLoading && hasAnimals) {
                SearchSection(
                    query = query,
                    onQueryChange = onQueryChange,
                    placeholder = stringResource(R.string.buscar_animales),
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (isPetsLoading) {
                    ShelterCardListSkeleton(modifier = Modifier.fillMaxSize())
                } else if (!hasAnimals) {
                    Text(
                        text = stringResource(R.string.shelter_sin_animales_disponibles),
                        modifier = Modifier.padding(horizontal = Dimens.spacingSm),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else if (animals.isEmpty() && isFiltering) {
                    Text(
                        text = stringResource(R.string.sin_resultados_animales),
                        modifier = Modifier.padding(horizontal = Dimens.spacingSm),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    ListCardView(
                        items = animals,
                        onItemClick = { animal -> onPetClick(animal.id) },
                        itemContent = { animal, onClick ->
                            CardViewList(
                                name = animal.name.orEmpty(),
                                subtitle = animalListSubtitle(animal),
                                photoUri = animal.mainPhoto,
                                onClick = onClick,
                            )
                        },
                    )
                }
            }
        }
    }
    }

    DataRefreshErrorDialog(
        error = refreshError,
        onDismiss = onRefreshErrorDismiss,
        onRetry = onRefresh,
    )

    if (showFilters) {
        AnimalFiltersBottomSheet(
            selectedSpecies = selectedSpecies,
            selectedSex = selectedSex,
            selectedCharacteristics = selectedCharacteristics,
            onSpeciesFilterChange = onSpeciesFilterChange,
            onSexFilterChange = onSexFilterChange,
            onCharacteristicToggle = onCharacteristicToggle,
            onClearFilters = onClearFilters,
            onDismiss = { showFilters = false },
            sheetState = filterSheetState,
        )
    }
}

@Composable
private fun ContactActionText(
    text: String,
    onClick: () -> Unit,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.primary,
        textDecoration = TextDecoration.Underline,
        modifier = Modifier.clickable(
            role = Role.Button,
            onClick = onClick,
        )
    )
}

private fun openPhoneApp(context: Context, phone: String) {
    openExternalApp(
        context = context,
        intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null)),
        errorMessage = context.getString(R.string.no_phone_app_available),
    )
}

private fun openEmailApp(context: Context, email: String) {
    val emailUri = Uri.fromParts("mailto", email, null)
    val gmailIntent = Intent(Intent.ACTION_SENDTO, emailUri).setPackage(GMAIL_PACKAGE)

    try {
        context.startActivity(gmailIntent)
    } catch (_: ActivityNotFoundException) {
        openExternalApp(
            context = context,
            intent = Intent(Intent.ACTION_SENDTO, emailUri),
            errorMessage = context.getString(R.string.no_email_app_available),
        )
    }
}

private fun openExternalApp(
    context: Context,
    intent: Intent,
    errorMessage: String,
) {
    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
    }
}

private const val GMAIL_PACKAGE = "com.google.android.gm"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnimalFiltersBottomSheet(
    selectedSpecies: Species?,
    selectedSex: Boolean?,
    selectedCharacteristics: Set<Characteristic>,
    onSpeciesFilterChange: (Species?) -> Unit,
    onSexFilterChange: (Boolean?) -> Unit,
    onCharacteristicToggle: (Characteristic) -> Unit,
    onClearFilters: () -> Unit,
    onDismiss: () -> Unit,
    sheetState: androidx.compose.material3.SheetState,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.spacingSm)
                .padding(bottom = Dimens.spacingXl),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
        ) {
            Text(
                text = stringResource(R.string.filtros),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )

            FilterSection(title = stringResource(R.string.especie)) {
                FilterChip(
                    selected = selectedSpecies == null,
                    onClick = { onSpeciesFilterChange(null) },
                    label = { Text(stringResource(R.string.todos)) }
                )
                Species.entries.forEach { species ->
                    FilterChip(
                        selected = selectedSpecies == species,
                        onClick = { onSpeciesFilterChange(species) },
                        label = { Text(speciesLabel(species)) }
                    )
                }
            }

            FilterSection(title = stringResource(R.string.sexo)) {
                FilterChip(
                    selected = selectedSex == null,
                    onClick = { onSexFilterChange(null) },
                    label = { Text(stringResource(R.string.todos)) }
                )
                FilterChip(
                    selected = selectedSex == false,
                    onClick = { onSexFilterChange(false) },
                    label = { Text(stringResource(R.string.macho)) }
                )
                FilterChip(
                    selected = selectedSex == true,
                    onClick = { onSexFilterChange(true) },
                    label = { Text(stringResource(R.string.hembra)) }
                )
            }

            FilterSection(title = stringResource(R.string.caracteristicas)) {
                Characteristic.entries.forEach { characteristic ->
                    FilterChip(
                        selected = characteristic in selectedCharacteristics,
                        onClick = { onCharacteristicToggle(characteristic) },
                        label = { Text(characteristicLabel(characteristic)) }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onClearFilters) {
                    Text(stringResource(R.string.limpiar_filtros))
                }
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.aplicar_filtros))
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FilterSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
        ) {
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShelterProfileViewPreview() {
    AdoptionTheme {
        ShelterProfileScreen(
            shelter = previewShelter(),
            animals = previewAnimals(),
            onBackClick = {},
            onPetClick = {},
        )
    }
}

@Preview(showBackground = true, name = "Lista animales (cargando)")
@Composable
fun ShelterProfilePetsLoadingPreview() {
    AdoptionTheme {
        ShelterProfileScreen(
            shelter = previewShelter(),
            animals = emptyList(),
            onBackClick = {},
            onPetClick = {},
            isPetsLoading = true,
        )
    }
}

private fun previewShelter(): ShelterEntity = ShelterEntity(
    "preview-shelter",
    "Protectora Norte",
    "B00000001",
    null,
    "contacto@protectoranorte.org",
    "Calle Mayor 1",
    "600000001",
    "https://forms.gle/preview",
)

private fun previewAnimals(): List<AnimalEntity> = listOf(
    AnimalEntity(
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
    ),
    AnimalEntity(
        "preview-animal-2",
        "Milo",
        false,
        null,
        emptyList(),
        0L,
        "Muy jugueton y sociable.",
        Species.GATO,
        listOf(Characteristic.JUGUETON),
        "preview-shelter",
        true,
    ),
)