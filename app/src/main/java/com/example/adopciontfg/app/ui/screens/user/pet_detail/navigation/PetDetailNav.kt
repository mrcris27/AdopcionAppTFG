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
    val formMissingMessage = stringResource(R.string.adopcion_formulario_no_configurado)

    LaunchedEffect(petId) {
        viewModel.loadAnimal(petId)
    }

    val onAdoptClick = {
        val url = uiState.adoptionFormUrl.trim()
        if (url.isBlank()) {
            Toast.makeText(context, formMissingMessage, Toast.LENGTH_LONG).show()
        } else {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        }
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
