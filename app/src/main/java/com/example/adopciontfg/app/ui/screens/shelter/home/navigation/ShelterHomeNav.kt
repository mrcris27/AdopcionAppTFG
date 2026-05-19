package com.example.adopciontfg.app.ui.screens.shelter.home.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.adopciontfg.app.ui.screens.shelter.pet_registration.PetRegistration
import com.example.adopciontfg.app.ui.screens.shelter.settings.ShelterSettingsScreen
import kotlinx.serialization.Serializable

@Composable
fun ShelterMainNavHost(
    navController: NavHostController,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = ShelterAddPetRoute,
        modifier = modifier,
    ) {
        composable<ShelterAddPetRoute> {
            PetRegistration()
        }
        composable<ShelterSettingsTabRoute> {
            ShelterSettingsScreen(
                onLogout = onLogout,
            )
        }
    }
}

@Serializable
object ShelterAddPetRoute

@Serializable
object ShelterSettingsTabRoute
