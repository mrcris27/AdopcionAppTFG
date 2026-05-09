package com.example.adopciontfg.app.ui.screens.components.bottom_tab

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.adopciontfg.app.ui.screens.pet_list.navigation.PetListRoute
import com.example.adopciontfg.app.ui.screens.settings.navigation.SettingsHubRoute
import com.example.adopciontfg.app.ui.screens.user_home_screen.navigation.UserScreenRoute

sealed class BottomTab(
    val route: Any,
    val icon: ImageVector
) {
    data object Home : BottomTab(
        route = UserScreenRoute,
        icon = Icons.Filled.Home
    )

    data object Pets : BottomTab(
        route = PetListRoute,
        icon = Icons.Filled.Pets
    )
    data object Settings : BottomTab(
        route = SettingsHubRoute,
        icon = Icons.Filled.Settings
    )

}