package com.example.adopciontfg.app.ui.navigation



import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier

import androidx.navigation.NavHostController

import androidx.navigation.compose.NavHost

import com.example.adopciontfg.app.ui.screens.user.pet_detail.navigation.PetDetailRoute

import com.example.adopciontfg.app.ui.screens.user.pet_detail.navigation.petDetail

import com.example.adopciontfg.app.ui.screens.shelter.settings.navigation.ShelterSettingsRoute

import com.example.adopciontfg.app.ui.screens.shelter.settings.navigation.shelterSettingsScreen

import com.example.adopciontfg.app.ui.screens.user.home.navigation.UserScreenRoute

import com.example.adopciontfg.app.ui.screens.user.home.navigation.userScreen

import com.example.adopciontfg.app.ui.screens.user.settings.navigation.UserSettingsRoute

import com.example.adopciontfg.app.ui.screens.user.settings.navigation.settingsHubScreen

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

            onAdoptClick = {},

        )



        shelterProfileScreen(

            onBackClick = { navController.popBackStack() },

            onPetClick = { petId ->

                navController.navigate(PetDetailRoute(petId))

            },

        )



        settingsHubScreen(

            onUserSettingsClick = { navController.navigate(UserSettingsRoute) },

            onShelterSettingsClick = { navController.navigate(ShelterSettingsRoute) },

        )



        userSettingsScreen(

            onBackClick = { navController.popBackStack() },

            onLogout = onLogout,

        )



        shelterSettingsScreen(

            onBackClick = { navController.popBackStack() },

            onLogout = onLogout,

        )

    }

}

