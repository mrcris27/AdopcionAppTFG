package com.example.adopciontfg.app.ui.screens.login_screen.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.adopciontfg.app.ui.screens.login_screen.LoginScreen
import kotlinx.serialization.Serializable


fun NavGraphBuilder.loginScreen(
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit
){
    composable<LoginScreenRoute> {
        LoginScreen(
            onBackClick = onBackClick,
            onContinueClick = onContinueClick
            )
    }
}


@Serializable
object LoginScreenRoute