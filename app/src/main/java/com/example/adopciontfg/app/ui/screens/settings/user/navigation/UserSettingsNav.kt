package com.example.adopciontfg.app.ui.screens.settings.user.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.adopciontfg.app.ui.screens.settings.user.UserSettingsScreen
import kotlinx.serialization.Serializable

fun NavGraphBuilder.userSettingsScreen(
    onBackClick: () -> Unit,
    onLogout: () -> Unit
) {
    composable<UserSettingsRoute> {
        UserSettingsScreen(
            onBackClick = onBackClick,
            onLogout = onLogout
        )
    }
}

@Serializable
object UserSettingsRoute
