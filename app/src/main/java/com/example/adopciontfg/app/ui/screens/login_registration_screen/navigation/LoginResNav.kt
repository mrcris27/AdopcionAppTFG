package com.example.adopciontfg.app.ui.screens.login_registration_screen.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.adopciontfg.app.ui.screens.login_registration_screen.LoginRegistrationScreen
import kotlinx.serialization.Serializable

fun NavGraphBuilder.loginResScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    composable<LoginResRoute> {
        LoginRegistrationScreen(
            onLoginClick = onLoginClick,
            onRegisterClick = onRegisterClick
        )
    }
}

@Serializable
object LoginResRoute