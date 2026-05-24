package com.example.adopciontfg.app.ui.screens.shelter.registration.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.adopciontfg.app.ui.screens.shelter.registration.ShelterRegistration
import kotlinx.serialization.Serializable


fun NavGraphBuilder.shelterRegistrationScreen(
    onRegisterClick: (
        name: String,
        cif: String,
        phone: String,
        address: String,
        profilePhotoUri: String,
        email: String,
        password: String
    ) -> Unit,
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