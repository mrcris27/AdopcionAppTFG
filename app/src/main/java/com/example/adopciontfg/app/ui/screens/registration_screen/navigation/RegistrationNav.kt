package com.example.adopciontfg.app.ui.screens.registration_screen.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.adopciontfg.app.ui.screens.registration_screen.RegistrationScreen
import kotlinx.serialization.Serializable

fun NavGraphBuilder.registrationScreen(
    onBackClick: () -> Unit,
    onRegisterUserClick: () -> Unit,
    onRegisterShelterClick: () -> Unit
){
    composable<RegistrationScreenRoute> {
        RegistrationScreen(
            onBackClick = onBackClick,
            onRegisterUserClick = onRegisterUserClick,
            onRegisterShelterClick = onRegisterShelterClick
        )
    }

}

@Serializable
object RegistrationScreenRoute