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
    onBackClick: () -> Unit,
    isSaving: Boolean = false
){
    composable<ShelterRegistrationRoute>{
        ShelterRegistration(
            onRegisterClick = onRegisterClick,
            onBackClick = onBackClick,
            isSaving = isSaving
        )
    }
}

@Serializable
object ShelterRegistrationRoute