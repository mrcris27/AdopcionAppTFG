package com.example.adopciontfg.app.ui.screens.shelter.animals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.adopciontfg.R
import com.example.adopciontfg.app.ui.components.skeleton.ShelterCardListSkeleton
import com.example.adopciontfg.app.ui.screens.components.DataRefreshErrorDialog
import com.example.adopciontfg.app.ui.screens.components.characteristicLabel
import com.example.adopciontfg.app.ui.screens.components.speciesLabel
import com.example.adopciontfg.app.ui.screens.shelter.animals.components.ShelterAnimalCard
import com.example.adopciontfg.data.local.entity.AnimalEntity
import com.example.adopciontfg.model.Characteristic
import com.example.adopciontfg.model.Species
import com.example.adopciontfg.ui.theme.AdoptionTheme
import com.example.adopciontfg.ui.theme.Dimens
import com.example.adopciontfg.ui.theme.elevatedSurface

@Composable
fun ShelterAnimalsScreen(
    onAddAnimalClick: () -> Unit,
    onEditAnimalClick: (String) -> Unit,
    viewModel: ShelterAnimalsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ShelterAnimalsContent(
        uiState = uiState,
        onQueryChange = viewModel::onQueryChange,
        onSpeciesFilterChange = viewModel::onSpeciesFilterChange,
        onSexFilterChange = viewModel::onSexFilterChange,
        onAdoptionStatusFilterChange = viewModel::onAdoptionStatusFilterChange,
        onCharacteristicToggle = viewModel::onCharacteristicToggle,
        onClearFilters = viewModel::clearFilters,
        onRefresh = viewModel::refreshAnimals,
        onRefreshErrorDismiss = viewModel::dismissRefreshError,
        onAddAnimalClick = onAddAnimalClick,
        onEditAnimalClick = onEditAnimalClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShelterAnimalsContent(
    uiState: ShelterAnimalsUiState,
    onQueryChange: (String) -> Unit,
    onSpeciesFilterChange: (Species?) -> Unit,
    onSexFilterChange: (Boolean?) -> Unit,
    onAdoptionStatusFilterChange: (ShelterAnimalAdoptionStatus?) -> Unit,
    onCharacteristicToggle: (Characteristic) -> Unit,
    onClearFilters: () -> Unit,
    onRefresh: () -> Unit,
    onRefreshErrorDismiss: () -> Unit,
    onAddAnimalClick: () -> Unit,
    onEditAnimalClick: (String) -> Unit,
) {
    var showFilters by remember { mutableStateOf(false) }
    val filterSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddAnimalClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.register_animal))
            }
        },
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                ShelterAnimalsOverview(
                    total = uiState.animals.size,
                    query = uiState.query,
                    hasActiveFilters = uiState.hasActiveFilters,
                    filtersEnabled = !uiState.isLoading && (uiState.animals.isNotEmpty() || uiState.hasActiveFilters),
                    onQueryChange = onQueryChange,
                    onFilterClick = { showFilters = true },
                    modifier = Modifier.padding(
                        horizontal = Dimens.screenPadding,
                        vertical = Dimens.spacingSm,
                    ),
                )

                Box(modifier = Modifier.fillMaxSize()) {
                    when {
                        uiState.shelterId == null -> {
                            EmptyMessage(
                                text = stringResource(R.string.shelter_no_session),
                                modifier = Modifier.align(Alignment.Center),
                            )
                        }
                        uiState.isLoading -> ShelterCardListSkeleton()
                        uiState.filteredAnimals.isEmpty() -> {
                            EmptyMessage(
                                text = if (uiState.animals.isEmpty()) {
                                    stringResource(R.string.shelter_no_animals)
                                } else {
                                    stringResource(R.string.no_animal_results)
                                },
                                modifier = Modifier.align(Alignment.Center),
                            )
                        }
                        else -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(Dimens.listItemSpacing),
                                contentPadding = PaddingValues(bottom = 88.dp),
                            ) {
                                items(uiState.filteredAnimals, key = { it.id }) { animal ->
                                    ShelterAnimalCard(
                                        animal = animal,
                                        onClick = { onEditAnimalClick(animal.id) },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        DataRefreshErrorDialog(
            error = uiState.refreshError,
            onDismiss = onRefreshErrorDismiss,
            onRetry = onRefresh,
        )
    }

    if (showFilters) {
        AnimalFiltersBottomSheet(
            selectedSpecies = uiState.selectedSpecies,
            selectedSex = uiState.selectedSex,
            selectedAdoptionStatus = uiState.selectedAdoptionStatus,
            selectedCharacteristics = uiState.selectedCharacteristics,
            onSpeciesFilterChange = onSpeciesFilterChange,
            onSexFilterChange = onSexFilterChange,
            onAdoptionStatusFilterChange = onAdoptionStatusFilterChange,
            onCharacteristicToggle = onCharacteristicToggle,
            onClearFilters = onClearFilters,
            onDismiss = { showFilters = false },
            sheetState = filterSheetState,
        )
    }
}

@Composable
private fun ShelterAnimalsOverview(
    total: Int,
    query: String,
    hasActiveFilters: Boolean,
    filtersEnabled: Boolean,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.elevatedSurface(),
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(Dimens.cardPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
        ) {
            Text(
                text = stringResource(R.string.registered_animals),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = stringResource(R.string.shelter_total_animals, total),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ShelterSearchField(
                    query = query,
                    onQueryChange = onQueryChange,
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.width(Dimens.spacingSm))
                IconButton(
                    onClick = onFilterClick,
                    enabled = filtersEnabled,
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
                            contentDescription = stringResource(R.string.open_animal_filters),
                            tint = if (hasActiveFilters) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ShelterSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        shape = MaterialTheme.shapes.large,
        placeholder = {
            Text(
                text = stringResource(R.string.search_my_animals),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        },
        trailingIcon = if (query.isNotBlank()) {
            {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = stringResource(R.string.clear_search),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        } else {
            null
        },
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.onSurface,
        ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.primary,
        ),
    )
}

@Composable
private fun EmptyMessage(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier.padding(Dimens.screenPadding),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnimalFiltersBottomSheet(
    selectedSpecies: Species?,
    selectedSex: Boolean?,
    selectedAdoptionStatus: ShelterAnimalAdoptionStatus?,
    selectedCharacteristics: Set<Characteristic>,
    onSpeciesFilterChange: (Species?) -> Unit,
    onSexFilterChange: (Boolean?) -> Unit,
    onAdoptionStatusFilterChange: (ShelterAnimalAdoptionStatus?) -> Unit,
    onCharacteristicToggle: (Characteristic) -> Unit,
    onClearFilters: () -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState,
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
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
        ) {
            Text(
                text = stringResource(R.string.filters),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )

            FilterSection(title = stringResource(R.string.species)) {
                FilterChip(
                    selected = selectedSpecies == null,
                    onClick = { onSpeciesFilterChange(null) },
                    label = { Text(stringResource(R.string.all)) },
                )
                Species.entries.forEach { species ->
                    FilterChip(
                        selected = selectedSpecies == species,
                        onClick = { onSpeciesFilterChange(species) },
                        label = { Text(speciesLabel(species)) },
                    )
                }
            }

            FilterSection(title = stringResource(R.string.sex)) {
                FilterChip(
                    selected = selectedSex == null,
                    onClick = { onSexFilterChange(null) },
                    label = { Text(stringResource(R.string.all)) },
                )
                FilterChip(
                    selected = selectedSex == false,
                    onClick = { onSexFilterChange(false) },
                    label = { Text(stringResource(R.string.male)) },
                )
                FilterChip(
                    selected = selectedSex == true,
                    onClick = { onSexFilterChange(true) },
                    label = { Text(stringResource(R.string.female)) },
                )
            }

            FilterSection(title = stringResource(R.string.adoption_status)) {
                FilterChip(
                    selected = selectedAdoptionStatus == null,
                    onClick = { onAdoptionStatusFilterChange(null) },
                    label = { Text(stringResource(R.string.all)) },
                )
                FilterChip(
                    selected = selectedAdoptionStatus == ShelterAnimalAdoptionStatus.AVAILABLE,
                    onClick = {
                        onAdoptionStatusFilterChange(ShelterAnimalAdoptionStatus.AVAILABLE)
                    },
                    label = { Text(stringResource(R.string.available_for_adoption)) },
                )
                FilterChip(
                    selected = selectedAdoptionStatus == ShelterAnimalAdoptionStatus.NOT_AVAILABLE,
                    onClick = {
                        onAdoptionStatusFilterChange(ShelterAnimalAdoptionStatus.NOT_AVAILABLE)
                    },
                    label = { Text(stringResource(R.string.not_available_for_adoption)) },
                )
            }

            FilterSection(title = stringResource(R.string.characteristics)) {
                Characteristic.entries.forEach { characteristic ->
                    FilterChip(
                        selected = characteristic in selectedCharacteristics,
                        onClick = { onCharacteristicToggle(characteristic) },
                        label = { Text(characteristicLabel(characteristic)) },
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(onClick = onClearFilters) {
                    Text(stringResource(R.string.clear_filters))
                }
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.apply_filters))
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
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
private fun ShelterAnimalsScreenPreview() {
    AdoptionTheme {
        val animals = previewAnimals()

        ShelterAnimalsContent(
            uiState = ShelterAnimalsUiState(
                shelterId = "1",
                shelterName = "Protectora Ejemplo",
                adoptionFormUrl = "https://forms.gle/ejemplo",
                isLoading = false,
                animals = animals,
                filteredAnimals = animals,
            ),
            onQueryChange = {},
            onSpeciesFilterChange = {},
            onSexFilterChange = {},
            onAdoptionStatusFilterChange = {},
            onCharacteristicToggle = {},
            onClearFilters = {},
            onRefresh = {},
            onRefreshErrorDismiss = {},
            onAddAnimalClick = {},
            onEditAnimalClick = {},
        )
    }
}

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
        "1",
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
        "1",
        true,
    ),
    AnimalEntity(
        "preview-animal-3",
        "Nala",
        true,
        null,
        emptyList(),
        0L,
        "Ya tiene familia asignada.",
        Species.PERRO,
        listOf(Characteristic.SOCIABLE_CON_PERROS),
        "1",
        false,
    ),
)