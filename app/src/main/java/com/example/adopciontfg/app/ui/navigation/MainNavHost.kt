package com.example.adopciontfg.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.adopciontfg.app.ui.screens.pet_detail_screen.navigation.PetDetailRoute
import com.example.adopciontfg.app.ui.screens.pet_detail_screen.navigation.petDetail
import com.example.adopciontfg.app.ui.screens.settings.navigation.settingsHubScreen
import com.example.adopciontfg.app.ui.screens.settings.shelter.navigation.ShelterSettingsRoute
import com.example.adopciontfg.app.ui.screens.settings.shelter.navigation.shelterSettingsScreen
import com.example.adopciontfg.app.ui.screens.settings.user.navigation.UserSettingsRoute
import com.example.adopciontfg.app.ui.screens.settings.user.navigation.userSettingsScreen
import com.example.adopciontfg.app.ui.screens.shelter_profile_screen.navigation.ShelterProfileRoute
import com.example.adopciontfg.app.ui.screens.shelter_profile_screen.navigation.shelterProfileScreen
import com.example.adopciontfg.app.ui.screens.user_home_screen.navigation.UserScreenRoute
import com.example.adopciontfg.app.ui.screens.user_home_screen.navigation.userScreen

@Composable
fun MainNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    NavHost(
        navController = navController,
        startDestination = UserScreenRoute,
        modifier = modifier
    ) {

        userScreen(
            onDetailClick = { shelter ->
                navController.navigate(ShelterProfileRoute(shelter.id))
            }
        )

        petDetail(
            onBackClick = { navController.popBackStack() },
            onAdoptClick = {}
        )

        shelterProfileScreen(
            onBackClick = { navController.popBackStack() },
            onPetClick = { petId ->
                navController.navigate(PetDetailRoute(petId))
            }
        )

        settingsHubScreen(
            onUserSettingsClick = { navController.navigate(UserSettingsRoute) },
            onShelterSettingsClick = { navController.navigate(ShelterSettingsRoute) }
        )

        userSettingsScreen(
            onBackClick = { navController.popBackStack() }
        )

        shelterSettingsScreen(
            onBackClick = { navController.popBackStack() }
        )
    }
}