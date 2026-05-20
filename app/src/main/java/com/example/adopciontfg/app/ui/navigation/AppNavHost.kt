package com.example.adopciontfg.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.adopciontfg.app.ui.screens.auth.welcome.navigation.LoginResRoute
import com.example.adopciontfg.app.ui.screens.auth.welcome.navigation.loginResScreen
import com.example.adopciontfg.app.ui.screens.auth.login.navigation.LoginScreenRoute
import com.example.adopciontfg.app.ui.screens.auth.login.navigation.loginScreen
import com.example.adopciontfg.app.ui.screens.auth.register.navigation.RegistrationScreenRoute
import com.example.adopciontfg.app.ui.screens.auth.register.navigation.registrationScreen
import com.example.adopciontfg.app.ui.screens.shelter.registration.navigation.ShelterRegistrationRoute
import com.example.adopciontfg.app.ui.screens.shelter.registration.navigation.shelterRegistrationScreen
import com.example.adopciontfg.app.ui.screens.shelter.home.ShelterHomeScreen
import com.example.adopciontfg.app.ui.screens.user.registration.navigation.UserRegistrationRoute
import com.example.adopciontfg.app.ui.screens.user.registration.navigation.userRegistrationScreen

@Composable
fun AppNavHost(navController: NavHostController) {
//    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = LoginResRoute
    ) {

        /* ---------------- AUTH ---------------- */

        loginResScreen(
            onLoginClick = {
                navController.navigate(LoginScreenRoute)
            },
            onRegisterClick = {
                navController.navigate(RegistrationScreenRoute)
            }
        )

        loginScreen(
            onBackClick = { navController.popBackStack() },
            onContinueClick = {
                navController.navigate(USER_MAIN_ROUTE) {
                    popUpTo(LoginScreenRoute) { inclusive = true }
                }
            },
//            authViewModel = authViewModel
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
            onRegisterClick = {},
            //            authViewModel = authViewModel
        )

        shelterRegistrationScreen(
            onRegisterClick = {
                navController.navigate(SHELTER_MAIN_ROUTE) {
                    popUpTo(ShelterRegistrationRoute) { inclusive = true }
                }
            },
            onBackClick = { navController.popBackStack() },
        )

        /* ---------------- MAIN ---------------- */

        composable(USER_MAIN_ROUTE) {
            AppScaffold(
                onLogout = { navigateToLogin(navController) },
            )
        }

        composable(SHELTER_MAIN_ROUTE) {
            ShelterHomeScreen(
                onLogout = { navigateToLogin(navController, fromShelter = true) },
            )
        }
    }
}

private const val USER_MAIN_ROUTE = "main"
private const val SHELTER_MAIN_ROUTE = "main/shelter"

private fun navigateToLogin(
    navController: NavHostController,
    fromShelter: Boolean = false,
) {
    val mainRoute = if (fromShelter) SHELTER_MAIN_ROUTE else USER_MAIN_ROUTE
    navController.navigate(LoginResRoute) {
        popUpTo(mainRoute) { inclusive = true }
        launchSingleTop = true
    }
}