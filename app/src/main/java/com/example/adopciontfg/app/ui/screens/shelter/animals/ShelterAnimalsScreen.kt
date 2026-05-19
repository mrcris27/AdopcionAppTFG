package com.example.adopciontfg.app.ui.screens.shelter.animals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.adopciontfg.R
import com.example.adopciontfg.app.ui.components.skeleton.ShelterCardListSkeleton
import com.example.adopciontfg.app.ui.screens.components.AppSectionTitle
import com.example.adopciontfg.app.ui.screens.components.AppTopAppBar
import com.example.adopciontfg.app.ui.screens.components.SearchSection
import com.example.adopciontfg.app.ui.screens.shelter.animals.components.ShelterAnimalCard
import com.example.adopciontfg.data.local.entity.AnimalEntity
import com.example.adopciontfg.data.sampleAnimals
import com.example.adopciontfg.model.AnimalStatus
import com.example.adopciontfg.ui.theme.AdoptionTheme
import com.example.adopciontfg.ui.theme.Dimens

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
            ShelterStatsRow(
                available = uiState.availableCount,
                reserved = uiState.reservedCount,
                adopted = uiState.adoptedCount,
                modifier = Modifier.padding(horizontal = Dimens.screenPadding, vertical = Dimens.spacingSm),
            )

            SearchSection(
                query = uiState.query,
                onQueryChange = onQueryChange,
                placeholder = stringResource(R.string.buscar_mis_animales),
            )

            StatusFilterChips(
                selected = uiState.statusFilter,
                onSelect = onStatusFilterChange,
                modifier = Modifier.padding(horizontal = Dimens.spacingLg, vertical = Dimens.spacingXs),
            )

            AppSectionTitle(
                text = stringResource(R.string.animales_registrados),
                modifier = Modifier.padding(horizontal = Dimens.screenPadding, vertical = Dimens.spacingSm),
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
    ) {
        StatChip(label = stringResource(R.string.disponibles), count = available)
        StatChip(label = stringResource(R.string.reservados), count = reserved)
        StatChip(label = stringResource(R.string.adoptados), count = adopted)
    }
}

@Composable
private fun StatChip(label: String, count: Int) {
    FilterChip(
        selected = false,
        onClick = {},
        label = {
            Text(
                text = "$label: $count",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
            )
        },
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StatusFilterChips(
    selected: ShelterAnimalFilter,
    onSelect: (ShelterAnimalFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
    ) {
        ShelterAnimalFilter.entries.forEach { filter ->
            FilterChip(
                selected = selected == filter,
                onClick = { onSelect(filter) },
                label = { Text(statusFilterLabel(filter)) },
            )
        }
    }
}

@Composable
private fun statusFilterLabel(filter: ShelterAnimalFilter): String = when (filter) {
    ShelterAnimalFilter.ALL -> stringResource(R.string.todos)
    ShelterAnimalFilter.AVAILABLE -> stringResource(R.string.disponibles)
    ShelterAnimalFilter.RESERVED -> stringResource(R.string.reservados)
    ShelterAnimalFilter.ADOPTED -> stringResource(R.string.adoptados)
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

@Preview(showBackground = true)
@Composable
private fun ShelterAnimalsScreenPreview() {
    AdoptionTheme {
        ShelterAnimalsContent(
            uiState = ShelterAnimalsUiState(
                shelterId = "1",
                shelterName = "Protectora Ejemplo",
                isLoading = false,
                animals = sampleAnimals.filter { it.shelterId == "1" },
                filteredAnimals = sampleAnimals.filter { it.shelterId == "1" },
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
