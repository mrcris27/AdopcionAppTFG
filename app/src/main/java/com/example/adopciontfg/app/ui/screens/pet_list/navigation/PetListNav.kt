package com.example.adopciontfg.app.ui.screens.pet_list.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.adopciontfg.app.ui.screens.pet_list.PetListScreen
import kotlinx.serialization.Serializable

fun NavGraphBuilder.petListScreen(
    onDetailClick: (String) -> Unit,
) {
    composable<PetListRoute> {
        PetListScreen(
            onDetailClick = onDetailClick
        )
    }
}

@Serializable
object PetListRoute