package com.example.adopciontfg.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.adopciontfg.app.ui.screens.login_registration_screen.navigation.LoginRoute
import com.example.adopciontfg.app.ui.screens.login_registration_screen.navigation.loginScreen
import com.example.adopciontfg.app.ui.screens.pet_detail_screen.navigation.PetDetailRoute
import com.example.adopciontfg.app.ui.screens.pet_detail_screen.navigation.petDetail
import com.example.adopciontfg.app.ui.screens.pet_list.navigation.petListScreen
import com.example.adopciontfg.app.ui.screens.shelter_profile_screen.navigation.ShelterProfileRoute
import com.example.adopciontfg.app.ui.screens.shelter_profile_screen.navigation.shelterProfileScreen
import com.example.adopciontfg.app.ui.screens.shelter_registration.navigation.ShelterRegistrationRoute
import com.example.adopciontfg.app.ui.screens.shelter_registration.navigation.shelterRegistrationScreen
import com.example.adopciontfg.app.ui.screens.user_home_screen.navigation.UserScreenRoute
import com.example.adopciontfg.app.ui.screens.user_home_screen.navigation.userScreen

@Composable
fun ScreensNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    NavHost(
        navController = navController,
        startDestination = LoginRoute,
        modifier = modifier
    ) {

        /* ---------------- AUTH ---------------- */

        loginScreen(
            onLoginClick = {
                navController.navigate(UserScreenRoute) {
                    popUpTo(LoginRoute)
                    launchSingleTop = true
                }
            },
            onRegisterClick = {
                navController.navigate(ShelterRegistrationRoute)
            }
        )

        shelterRegistrationScreen(
            onRegisterClick = {
                navController.navigate(UserScreenRoute) {
                    popUpTo(LoginRoute)
                    launchSingleTop = true
                }
            },
            onCancelClick = {
                navController.popBackStack()
            }
        )

        /* ---------------- HOME ---------------- */

        userScreen(
            onDetailClick = {
                navController.navigate(ShelterProfileRoute)
            },
            navController = navController
        )

        /* ---------------- PETS ---------------- */

        petListScreen(
            onDetailClick = { petId ->
                navController.navigate(PetDetailRoute(petId)) {
                    launchSingleTop = true
                }
            }
        )

        petDetail(
            onBackClick = {
                navController.popBackStack()
            },
            onAdoptClick = {
                // acción adoptar
            }
        )
        shelterProfileScreen(
            onBackClick = {
                navController.popBackStack()
            }
        )
    }
}