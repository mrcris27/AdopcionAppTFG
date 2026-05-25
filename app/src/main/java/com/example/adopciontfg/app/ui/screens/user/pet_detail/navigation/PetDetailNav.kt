package com.example.adopciontfg.app.ui.screens.user.pet_detail.navigation

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.adopciontfg.R
import com.example.adopciontfg.app.ui.screens.user.pet_detail.PetDetailScreen
import com.example.adopciontfg.app.ui.screens.user.pet_detail.PetDetailViewModel
import kotlinx.serialization.Serializable

fun NavGraphBuilder.petDetail(
    onBackClick: () -> Unit,
) {
    composable<PetDetailRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<PetDetailRoute>()
        PetDetailRouteContent(
            petId = route.petId,
            onBackClick = onBackClick,
        )
    }
}

@Composable
private fun PetDetailRouteContent(
    petId: String,
    onBackClick: () -> Unit,
    viewModel: PetDetailViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val formMissingMessage = stringResource(R.string.adoption_form_not_configured)
    val noBrowserMessage = stringResource(R.string.no_browser_app_available)

    LaunchedEffect(petId) {
        viewModel.loadAnimal(petId)
    }

    val onAdoptClick: () -> Unit = {
        val url = uiState.adoptionFormUrl.trim()
        if (url.isBlank()) {
            Toast.makeText(context, formMissingMessage, Toast.LENGTH_LONG).show()
        } else {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addCategory(Intent.CATEGORY_BROWSABLE)
            }
            runCatching {
                context.startActivity(intent)
            }.onFailure {
                Toast.makeText(context, noBrowserMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    val animal = uiState.animal
    when {
        uiState.isLoading && animal == null -> PetDetailScreen(
            animal = null,
            onBackClick = onBackClick,
            onAdoptClick = onAdoptClick,
            isLoading = true,
            isRefreshing = uiState.isRefreshing,
            refreshError = uiState.refreshError,
            onRefresh = viewModel::refreshAnimal,
            onRefreshErrorDismiss = viewModel::dismissRefreshError,
        )
        animal != null -> PetDetailScreen(
            animal = animal,
            onBackClick = onBackClick,
            onAdoptClick = onAdoptClick,
            isAdoptActionEnabled = !uiState.isAdoptionFormLoading,
            isRefreshing = uiState.isRefreshing,
            refreshError = uiState.refreshError,
            onRefresh = viewModel::refreshAnimal,
            onRefreshErrorDismiss = viewModel::dismissRefreshError,
        )
        else -> PetDetailScreen(
            animal = null,
            onBackClick = onBackClick,
            onAdoptClick = onAdoptClick,
            isRefreshing = uiState.isRefreshing,
            refreshError = uiState.refreshError,
            onRefresh = viewModel::refreshAnimal,
            onRefreshErrorDismiss = viewModel::dismissRefreshError,
        )
    }
}

@Serializable
data class PetDetailRoute(
    val petId: String,
)
