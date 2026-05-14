package com.example.adopciontfg.app.ui.screens.pet_detail_screen.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.adopciontfg.app.ui.screens.pet_detail_screen.PetDetailScreen
import com.example.adopciontfg.data.Pet
import com.example.adopciontfg.model.Characteristic
import com.example.adopciontfg.model.Species
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
        photos = listOf("", "", "", ""),
        description = "Max es un perro muy cariñoso y juguetón. Le encanta pasear y jugar con la pelota.",
        species = Species.PERRO,
        characteristics = listOf(Characteristic.SOCIABLE_CON_PERROS, Characteristic.JUGUETON),
        birthDate = 1672531200000L, // ejemplo timestamp
        mainPhoto = "",
        shelterId = "1"
    )
}

@Serializable
data class PetDetailRoute(
    val petId: String
)