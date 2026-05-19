package com.example.adopciontfg.app.ui.screens.user.pet_detail.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.adopciontfg.app.ui.screens.user.pet_detail.PetDetailScreen
import com.example.adopciontfg.app.ui.screens.user.pet_detail.PetDetailViewModel
import kotlinx.serialization.Serializable

fun NavGraphBuilder.petDetail(
    onBackClick: () -> Unit,
    onAdoptClick: () -> Unit,
) {
    composable<PetDetailRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<PetDetailRoute>()
        PetDetailRouteContent(
            petId = route.petId,
            onBackClick = onBackClick,
            onAdoptClick = onAdoptClick,
        )
    }
}

@Composable
private fun PetDetailRouteContent(
    petId: String,
    onBackClick: () -> Unit,
    onAdoptClick: () -> Unit,
    viewModel: PetDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(petId) {
        viewModel.loadAnimal(petId)
    }

    val animal = uiState.animal
    when {
        uiState.isLoading && animal == null -> PetDetailScreen(
            animal = null,
            onBackClick = onBackClick,
            onAdoptClick = onAdoptClick,
            isLoading = true,
        )
        animal != null -> PetDetailScreen(
            animal = animal,
            onBackClick = onBackClick,
            onAdoptClick = onAdoptClick,
        )
    }
}

@Serializable
data class PetDetailRoute(
    val petId: String,
)
