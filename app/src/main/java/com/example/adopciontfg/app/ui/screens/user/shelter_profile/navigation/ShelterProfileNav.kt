package com.example.adopciontfg.app.ui.screens.user.shelter_profile.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.adopciontfg.app.ui.components.skeleton.ShelterProfileScreenSkeleton
import com.example.adopciontfg.app.ui.screens.user.shelter_profile.ShelterProfileScreen
import com.example.adopciontfg.app.ui.screens.user.shelter_profile.ShelterProfileViewModel
import kotlinx.serialization.Serializable

fun NavGraphBuilder.shelterProfileScreen(
    onBackClick: () -> Unit,
    onPetClick: (String) -> Unit,
) {
    composable<ShelterProfileRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<ShelterProfileRoute>()
        ShelterProfileRouteContent(
            shelterId = route.shelterId,
            onBackClick = onBackClick,
            onPetClick = onPetClick,
        )
    }
}

@Composable
private fun ShelterProfileRouteContent(
    shelterId: String,
    onBackClick: () -> Unit,
    onPetClick: (String) -> Unit,
    viewModel: ShelterProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(shelterId) {
        viewModel.loadShelter(shelterId)
    }

    val shelter = uiState.shelter
    when {
        uiState.isLoading && shelter == null -> ShelterProfileScreenSkeleton()
        shelter != null -> ShelterProfileScreen(
            shelter = shelter,
            animals = uiState.filteredAnimals,
            hasAnimals = uiState.animals.isNotEmpty(),
            query = uiState.query,
            selectedSpecies = uiState.selectedSpecies,
            selectedSex = uiState.selectedSex,
            selectedCharacteristics = uiState.selectedCharacteristics,
            hasActiveFilters = uiState.hasActiveFilters,
            isFiltering = uiState.isFiltering,
            onBackClick = onBackClick,
            onPetClick = onPetClick,
            onQueryChange = viewModel::onQueryChange,
            onSpeciesFilterChange = viewModel::onSpeciesFilterChange,
            onSexFilterChange = viewModel::onSexFilterChange,
            onCharacteristicToggle = viewModel::onCharacteristicToggle,
            onClearFilters = viewModel::clearFilters,
            isPetsLoading = uiState.isPetsLoading,
            isRefreshing = uiState.isRefreshing,
            refreshError = uiState.refreshError,
            onRefresh = viewModel::refreshShelterProfile,
            onRefreshErrorDismiss = viewModel::dismissRefreshError,
        )
    }
}

@Serializable
data class ShelterProfileRoute(
    val shelterId: String,
)
