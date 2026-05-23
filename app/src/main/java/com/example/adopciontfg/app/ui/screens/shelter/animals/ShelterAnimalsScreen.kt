package com.example.adopciontfg.app.ui.screens.shelter.animals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import com.example.adopciontfg.app.ui.screens.components.AppTopAppBar
import com.example.adopciontfg.app.ui.screens.shelter.animals.components.ShelterAnimalCard
import com.example.adopciontfg.data.local.entity.AnimalEntity
import com.example.adopciontfg.model.AnimalStatus
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
        onStatusFilterChange = viewModel::onStatusFilterChange,
        onAddAnimalClick = onAddAnimalClick,
        onEditAnimalClick = onEditAnimalClick,
        onQuickStatusChange = viewModel::updateAnimalStatus,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun ShelterAnimalsContent(
    uiState: ShelterAnimalsUiState,
    onQueryChange: (String) -> Unit,
    onStatusFilterChange: (ShelterAnimalFilter) -> Unit,
    onAddAnimalClick: () -> Unit,
    onEditAnimalClick: (String) -> Unit,
    onQuickStatusChange: (String, AnimalStatus) -> Unit,
) {
    val title = uiState.shelterName.ifBlank { stringResource(R.string.mis_animales) }
    var showFilters by remember { mutableStateOf(false) }
    val filterSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val hasActiveFilters = uiState.statusFilter != ShelterAnimalFilter.ALL

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopAppBar(title = title)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddAnimalClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.registrar_animal))
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            ShelterAnimalsOverview(
                total = uiState.animals.size,
                available = uiState.availableCount,
                reserved = uiState.reservedCount,
                adopted = uiState.adoptedCount,
                query = uiState.query,
                onQueryChange = onQueryChange,
                hasActiveFilters = hasActiveFilters,
                onOpenFilters = { showFilters = true },
                modifier = Modifier.padding(
                    horizontal = Dimens.screenPadding,
                    vertical = Dimens.spacingSm,
                ),
            )

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.shelterId == null -> {
                        EmptyMessage(
                            text = stringResource(R.string.shelter_sin_sesion),
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }
                    uiState.isLoading -> ShelterCardListSkeleton()
                    uiState.filteredAnimals.isEmpty() -> {
                        EmptyMessage(
                            text = if (uiState.animals.isEmpty()) {
                                stringResource(R.string.shelter_sin_animales)
                            } else {
                                stringResource(R.string.sin_resultados_animales)
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
                                    onStatusChange = { status ->
                                        onQuickStatusChange(animal.id, status)
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFilters) {
        ShelterAnimalFiltersBottomSheet(
            selected = uiState.statusFilter,
            onSelect = onStatusFilterChange,
            onClearFilters = { onStatusFilterChange(ShelterAnimalFilter.ALL) },
            onDismiss = { showFilters = false },
            sheetState = filterSheetState,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShelterAnimalsOverview(
    total: Int,
    available: Int,
    reserved: Int,
    adopted: Int,
    query: String,
    onQueryChange: (String) -> Unit,
    hasActiveFilters: Boolean,
    onOpenFilters: () -> Unit,
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.animales_registrados),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = stringResource(R.string.shelter_total_animales, total),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                FilterIconButton(
                    hasActiveFilters = hasActiveFilters,
                    onOpenFilters = onOpenFilters,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ShelterSearchField(
                    query = query,
                    onQueryChange = onQueryChange,
                    modifier = Modifier.weight(1f),
                )
            }

            ShelterStatsRow(
                available = available,
                reserved = reserved,
                adopted = adopted,
            )
        }
    }
}

@Composable
private fun FilterIconButton(
    hasActiveFilters: Boolean,
    onOpenFilters: () -> Unit,
) {
    IconButton(onClick = onOpenFilters) {
        BadgedBox(
            badge = {
                if (hasActiveFilters) {
                    Badge()
                }
            },
        ) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = stringResource(R.string.abrir_filtros_animales),
                tint = if (hasActiveFilters) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
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
                text = stringResource(R.string.buscar_mis_animales),
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
                        contentDescription = stringResource(R.string.limpiar_busqueda),
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
private fun ShelterStatsRow(
    available: Int,
    reserved: Int,
    adopted: Int,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs),
    ) {
        StatPill(label = stringResource(R.string.disponibles), count = available)
        StatPill(label = stringResource(R.string.reservados), count = reserved)
        StatPill(label = stringResource(R.string.adoptados), count = adopted)
    }
}

@Composable
private fun StatPill(label: String, count: Int) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Dimens.spacingSm, vertical = Dimens.spacingXs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = " $label",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShelterAnimalFiltersBottomSheet(
    selected: ShelterAnimalFilter,
    onSelect: (ShelterAnimalFilter) -> Unit,
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
                .padding(horizontal = Dimens.spacingLg)
                .padding(bottom = Dimens.spacingXl),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
        ) {
            Text(
                text = stringResource(R.string.filtros),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )

            FilterSection(title = stringResource(R.string.estado_publicacion)) {
                ShelterAnimalFilter.entries.forEach { filter ->
                    FilterChip(
                        selected = selected == filter,
                        onClick = { onSelect(filter) },
                        label = { Text(statusFilterLabel(filter)) },
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
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

@Composable
private fun statusFilterLabel(filter: ShelterAnimalFilter): String = when (filter) {
    ShelterAnimalFilter.ALL -> stringResource(R.string.todos)
    ShelterAnimalFilter.AVAILABLE -> stringResource(R.string.disponibles)
    ShelterAnimalFilter.RESERVED -> stringResource(R.string.reservados)
    ShelterAnimalFilter.ADOPTED -> stringResource(R.string.adoptados)
    ShelterAnimalFilter.UNAVAILABLE -> stringResource(R.string.no_disponibles)
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
/*
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
                availableCount = 2,
                reservedCount = 0,
                adoptedCount = 1,
            ),
            onQueryChange = {},
            onStatusFilterChange = {},
            onAddAnimalClick = {},
            onEditAnimalClick = {},
            onQuickStatusChange = { _, _ -> },
        )
    }
}
/*
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
        AnimalStatus.AVAILABLE,
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
        AnimalStatus.AVAILABLE,
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
        AnimalStatus.ADOPTED,
    ),
)
*/