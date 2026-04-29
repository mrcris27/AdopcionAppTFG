package com.example.adopciontfg.app.ui.screens.shelter_registration.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.adopciontfg.app.ui.screens.shelter_registration.ShelterRegistration
import com.example.adopciontfg.app.ui.screens.user_home_screen.UserHomeScreen
import com.example.adopciontfg.app.ui.screens.user_home_screen.navigation.UserScreenRoute
import kotlinx.serialization.Serializable


fun NavGraphBuilder.shelterRegistrationScreen(
    onRegisterClick: () -> Unit,
    onCancelClick: () -> Unit
){
    composable<ShelterRegistrationRoute>{
        ShelterRegistration(
            onRegisterClick = onRegisterClick,
            onCancelClick = onCancelClick
        )
    }
}

@Serializable
object ShelterRegistrationRoute