package com.example.adopciontfg.app.ui.screens.shelter_profile_screen.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.adopciontfg.app.ui.screens.shelter_profile_screen.ShelterProfileScreen
import com.example.adopciontfg.data.shelterByIdOrDefault
import kotlinx.serialization.Serializable

fun NavGraphBuilder.shelterProfileScreen(
    onBackClick: () -> Unit,
    onPetClick: (String) -> Unit,
) {
    composable<ShelterProfileRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<ShelterProfileRoute>()
        val shelter = shelterByIdOrDefault(route.shelterId)
        ShelterProfileScreen(
            shelter = shelter,
            onBackClick = onBackClick,
            onPetClick = onPetClick,
        )
    }
}

@Serializable
data class ShelterProfileRoute(
    val shelterId: String,
)
