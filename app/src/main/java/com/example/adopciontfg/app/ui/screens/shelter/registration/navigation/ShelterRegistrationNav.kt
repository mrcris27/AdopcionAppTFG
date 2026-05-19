package com.example.adopciontfg.app.ui.screens.shelter.registration.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.adopciontfg.app.ui.screens.shelter.registration.ShelterRegistration
import kotlinx.serialization.Serializable


fun NavGraphBuilder.shelterRegistrationScreen(
    onRegisterClick: () -> Unit,
    onBackClick: () -> Unit
){
    composable<ShelterRegistrationRoute>{
        ShelterRegistration(
            onRegisterClick = onRegisterClick,
            onBackClick = onBackClick
        )
    }
}

@Serializable
object ShelterRegistrationRoute