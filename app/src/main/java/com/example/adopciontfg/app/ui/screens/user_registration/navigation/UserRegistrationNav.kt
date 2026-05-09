package com.example.adopciontfg.app.ui.screens.user_registration.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.adopciontfg.app.ui.screens.user_registration.UserRegistration
import kotlinx.serialization.Serializable

fun NavGraphBuilder.userRegistrationScreen(
    onBackClick: () -> Unit,
    onRegisterClick: () -> Unit
){
    composable<UserRegistrationRoute> {
        UserRegistration(
            onBackClick = onBackClick,
            onRegisterClick = onRegisterClick
        )
    }
}

@Serializable
object UserRegistrationRoute