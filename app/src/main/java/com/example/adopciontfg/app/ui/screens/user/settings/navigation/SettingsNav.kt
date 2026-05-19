package com.example.adopciontfg.app.ui.screens.user.settings.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.adopciontfg.app.ui.screens.user.settings.SettingsHubScreen
import kotlinx.serialization.Serializable

fun NavGraphBuilder.settingsHubScreen(
    onUserSettingsClick: () -> Unit,
    onShelterSettingsClick: () -> Unit
) {
    composable<SettingsHubRoute> {
        SettingsHubScreen(
            onUserSettingsClick = onUserSettingsClick,
            onShelterSettingsClick = onShelterSettingsClick
        )
    }
}

@Serializable
object SettingsHubRoute
