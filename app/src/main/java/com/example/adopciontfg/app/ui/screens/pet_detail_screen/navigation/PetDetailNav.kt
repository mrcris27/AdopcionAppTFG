package com.example.adopciontfg.app.ui.screens.pet_detail_screen.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.adopciontfg.app.ui.screens.pet_detail_screen.PetDetailScreen
import com.example.adopciontfg.data.Pet
import kotlinx.serialization.Serializable

fun NavGraphBuilder.petDetail(
    onBackClick: () -> Unit,
    onAdoptClick: () -> Unit,

) {
    composable<PetDetailRoute> { backStackEntry ->

        val route = backStackEntry.toRoute<PetDetailRoute>()

        // SIMULAS obtener el pet por id
        val pet = getPetById(route.petId)

        PetDetailScreen(
            pet = pet,
            onAdoptClick = onAdoptClick,
            onBackClick = onBackClick,
        )
    }
}

fun getPetById(id: String): Pet {
    return Pet(
        id = id,
        name = "Max",
        age = 3,
        breed = "Labrador",
        gender = "Macho",
        photos = listOf("", "", "", "")
    )
}

@Serializable
data class PetDetailRoute(
    val petId: String
)