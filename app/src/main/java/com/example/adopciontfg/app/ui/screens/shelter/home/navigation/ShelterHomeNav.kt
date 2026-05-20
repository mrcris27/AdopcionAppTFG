package com.example.adopciontfg.app.ui.screens.shelter.home.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.adopciontfg.app.ui.screens.shelter.animals.ShelterAnimalsScreen
import com.example.adopciontfg.app.ui.screens.shelter.pet_registration.ShelterPetFormScreen
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
        startDestination = ShelterAnimalsRoute,
        modifier = modifier,
    ) {
        composable<ShelterAnimalsRoute> {
            ShelterAnimalsScreen(
                onAddAnimalClick = { navController.navigate(ShelterAddPetRoute) },
                onEditAnimalClick = { animalId ->
                    navController.navigate(ShelterEditPetRoute(animalId))
                },
            )
        }
        composable<ShelterAddPetRoute> {
            ShelterPetFormScreen(
                animalId = null,
                onBackClick = { navController.popBackStack() },
                onSaved = {
                    navController.navigate(ShelterAnimalsRoute) {
                        popUpTo(ShelterAnimalsRoute) { inclusive = false }
                        launchSingleTop = true
                    }
                },
            )
        }
        composable<ShelterEditPetRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<ShelterEditPetRoute>()
            ShelterPetFormScreen(
                animalId = route.animalId,
                onBackClick = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
            )
        }
        composable<ShelterSettingsTabRoute> {
            ShelterSettingsScreen(
                onLogout = onLogout,
            )
        }
    }
}

@Serializable
object ShelterAnimalsRoute

@Serializable
object ShelterAddPetRoute

@Serializable
data class ShelterEditPetRoute(
    val animalId: String,
)

@Serializable
object ShelterSettingsTabRoute
