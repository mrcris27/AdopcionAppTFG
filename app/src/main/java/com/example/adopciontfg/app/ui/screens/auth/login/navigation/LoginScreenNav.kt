package com.example.adopciontfg.app.ui.screens.auth.login.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.adopciontfg.app.ui.screens.auth.login.LoginScreen
import kotlinx.serialization.Serializable


fun NavGraphBuilder.loginScreen(
    onBackClick: () -> Unit,
    onContinueClick: (email: String, password: String) -> Unit,
    isLoading: Boolean = false,
) {
    composable<LoginScreenRoute> {
        LoginScreen(
            onBackClick = onBackClick,
            onContinueClick = onContinueClick,
            isLoading = isLoading,
        )
    }
}


@Serializable
object LoginScreenRoute