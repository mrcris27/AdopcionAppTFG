package com.example.adopciontfg.app.ui.screens.settings.shelter.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.adopciontfg.app.ui.screens.settings.shelter.ShelterSettingsScreen
import kotlinx.serialization.Serializable

fun NavGraphBuilder.shelterSettingsScreen(onBackClick: () -> Unit) {
    composable<ShelterSettingsRoute> {
        ShelterSettingsScreen(onBackClick = onBackClick)
    }
}

@Serializable
object ShelterSettingsRoute
