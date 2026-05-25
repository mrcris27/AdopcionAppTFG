package com.example.adopciontfg.app.ui.navigation



import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier

import androidx.navigation.NavHostController

import androidx.navigation.compose.NavHost

import com.example.adopciontfg.app.ui.screens.user.pet_detail.navigation.PetDetailRoute

import com.example.adopciontfg.app.ui.screens.user.pet_detail.navigation.petDetail

import com.example.adopciontfg.app.ui.screens.user.home.navigation.UserScreenRoute

import com.example.adopciontfg.app.ui.screens.user.home.navigation.userScreen

import com.example.adopciontfg.app.ui.screens.user.settings.navigation.userSettingsScreen

import com.example.adopciontfg.app.ui.screens.user.shelter_profile.navigation.ShelterProfileRoute

import com.example.adopciontfg.app.ui.screens.user.shelter_profile.navigation.shelterProfileScreen



@Composable

fun MainNavHost(

    navController: NavHostController,

    onLogout: () -> Unit,

    modifier: Modifier = Modifier,

) {

    NavHost(

        navController = navController,

        startDestination = UserScreenRoute,

        modifier = modifier,

    ) {

        userScreen(

            onDetailClick = { shelter ->

                navController.navigate(ShelterProfileRoute(shelter.id))

            },

        )



        petDetail(
            onBackClick = { navController.popBackStack() },
        )



        shelterProfileScreen(

            onBackClick = { navController.popBackStack() },

            onPetClick = { petId ->

                navController.navigate(PetDetailRoute(petId))

            },

        )



        userSettingsScreen(
            onLogout = onLogout,

        )



    }

}

