package com.example.adopciontfg.app.ui.screens.user.settings.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.adopciontfg.app.ui.screens.user.settings.UserSettingsScreen
import kotlinx.serialization.Serializable

fun NavGraphBuilder.userSettingsScreen(
    onLogout: () -> Unit
) {
    composable<UserSettingsRoute> {
        UserSettingsScreen(
            onLogout = onLogout
        )
    }
}

@Serializable
object UserSettingsRoute
