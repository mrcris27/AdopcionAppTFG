package com.example.adopciontfg.app.ui.screens.auth.login.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.adopciontfg.app.ui.screens.auth.login.LoginScreen
import kotlinx.serialization.Serializable


fun NavGraphBuilder.loginScreen(
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
    onShelterPreviewClick: () -> Unit,
){
    composable<LoginScreenRoute> {
        LoginScreen(
            onBackClick = onBackClick,
            onContinueClick = onContinueClick,
            onShelterPreviewClick = onShelterPreviewClick,
//            authViewModel = authViewModel
            )
    }
}


@Serializable
object LoginScreenRoute