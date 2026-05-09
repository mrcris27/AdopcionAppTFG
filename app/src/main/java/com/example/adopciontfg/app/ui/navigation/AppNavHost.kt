package com.example.adopciontfg.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.adopciontfg.app.ui.screens.login_registration_screen.navigation.LoginResRoute
import com.example.adopciontfg.app.ui.screens.login_registration_screen.navigation.loginResScreen
import com.example.adopciontfg.app.ui.screens.login_screen.navigation.LoginScreenRoute
import com.example.adopciontfg.app.ui.screens.login_screen.navigation.loginScreen
import com.example.adopciontfg.app.ui.screens.registration_screen.navigation.RegistrationScreenRoute
import com.example.adopciontfg.app.ui.screens.registration_screen.navigation.registrationScreen
import com.example.adopciontfg.app.ui.screens.shelter_registration.navigation.ShelterRegistrationRoute
import com.example.adopciontfg.app.ui.screens.shelter_registration.navigation.shelterRegistrationScreen
import com.example.adopciontfg.app.ui.screens.user_registration.navigation.UserRegistrationRoute
import com.example.adopciontfg.app.ui.screens.user_registration.navigation.userRegistrationScreen

@Composable
fun AppNavHost(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = LoginResRoute
    ) {

        /* ---------------- AUTH (SIN Scaffold) ---------------- */

        loginResScreen(
            onLoginClick = {
                navController.navigate(LoginScreenRoute) {
                    popUpTo(LoginResRoute) { inclusive = true }
                }
            },
            onRegisterClick = {
                navController.navigate(RegistrationScreenRoute)
            }
        )

        loginScreen(
            onBackClick = { navController.popBackStack() },
            onContinueClick = {
                navController.navigate("main") {
                    popUpTo(LoginScreenRoute) { inclusive = true }
                }
            }
        )

        registrationScreen(
            onBackClick = { navController.popBackStack() },
            onRegisterUserClick = {
                navController.navigate(UserRegistrationRoute)
            },
            onRegisterShelterClick = {
                navController.navigate(ShelterRegistrationRoute)
            }
        )

        userRegistrationScreen(
            onBackClick = { navController.popBackStack() },
            onRegisterClick = {}
        )

        shelterRegistrationScreen(
            onRegisterClick = {},
            onBackClick = { navController.popBackStack() }
        )

        /* ---------------- MAIN (CON Scaffold) ---------------- */

        composable("main") {
            AppScaffold() // 👈 IMPORTANTE: sin pasar navController
        }
    }
}