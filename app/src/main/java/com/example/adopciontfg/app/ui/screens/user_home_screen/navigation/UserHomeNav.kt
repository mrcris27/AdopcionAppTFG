package com.example.adopciontfg.app.ui.screens.user_home_screen.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.adopciontfg.app.ui.screens.login_registration_screen.LoginRegistrationScreen
import com.example.adopciontfg.app.ui.screens.user_home_screen.UserHomeScreen
import com.example.adopciontfg.data.Shelter
import kotlinx.serialization.Serializable

fun NavGraphBuilder.userScreen(
    navController: NavHostController,
    onDetailClick: (Shelter) -> Unit,

    ){
    composable<UserScreenRoute>{
        UserHomeScreen(
            onDetailClick = onDetailClick,
            navController = navController
        )
    }
}

@Serializable
object UserScreenRoute