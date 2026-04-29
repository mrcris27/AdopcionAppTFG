package com.example.adopciontfg.app.ui.screens.shelter_profile_screen.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.adopciontfg.app.ui.screens.shelter_profile_screen.ShelterProfileScreen
import kotlinx.serialization.Serializable


fun NavGraphBuilder.shelterProfileScreen(
    onBackClick: () -> Unit,
    ){
    composable<ShelterProfileRoute>{
        ShelterProfileScreen(
            onBackClick = onBackClick
        )

    }
}

@Serializable
object ShelterProfileRoute