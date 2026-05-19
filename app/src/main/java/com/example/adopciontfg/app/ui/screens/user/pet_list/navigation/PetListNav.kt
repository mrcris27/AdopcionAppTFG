package com.example.adopciontfg.app.ui.screens.user.pet_list.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.adopciontfg.app.ui.screens.user.pet_list.PetListScreen
import com.example.adopciontfg.app.ui.screens.user.pet_list.PetListViewModel
import kotlinx.serialization.Serializable

fun NavGraphBuilder.petListScreen(
    onDetailClick: (String) -> Unit,
) {
    composable<PetListRoute> {
        PetListRouteContent(onDetailClick = onDetailClick)
    }
}

@Composable
private fun PetListRouteContent(
    onDetailClick: (String) -> Unit,
    viewModel: PetListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PetListScreen(
        uiState = uiState,
        onQueryChange = viewModel::onQueryChange,
        onDetailClick = onDetailClick,
    )
}

@Serializable
object PetListRoute
