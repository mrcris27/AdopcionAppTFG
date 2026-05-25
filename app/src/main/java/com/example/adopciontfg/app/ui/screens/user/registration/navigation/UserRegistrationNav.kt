package com.example.adopciontfg.app.ui.screens.user.registration.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.adopciontfg.app.ui.screens.user.registration.UserRegistration
import kotlinx.serialization.Serializable

fun NavGraphBuilder.userRegistrationScreen(
    onBackClick: () -> Unit,
    onRegisterClick: (
        name: String,
        surname: String,
        email: String,
        biography: String,
        profilePhotoUri: String,
        password: String
    ) -> Unit,
    isSaving: Boolean = false
){
    composable<UserRegistrationRoute> {
        UserRegistration(
            onBackClick = onBackClick,
            onRegisterClick = onRegisterClick,
            isSaving = isSaving,
            //            authViewModel = authViewModel
        )
    }
}

@Serializable
object UserRegistrationRoute