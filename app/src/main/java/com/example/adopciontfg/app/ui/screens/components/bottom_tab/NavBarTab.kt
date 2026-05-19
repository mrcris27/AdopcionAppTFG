package com.example.adopciontfg.app.ui.screens.components.bottom_tab

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.adopciontfg.R
import com.example.adopciontfg.app.ui.screens.shelter.home.navigation.ShelterAnimalsRoute
import com.example.adopciontfg.app.ui.screens.shelter.home.navigation.ShelterSettingsTabRoute
import com.example.adopciontfg.app.ui.screens.user.home.navigation.UserScreenRoute
import com.example.adopciontfg.app.ui.screens.user.settings.navigation.SettingsHubRoute

enum class NavBarTab(
    val route: Any,
    val icon: ImageVector,
    @StringRes val labelRes: Int,
) {
    UserHome(
        route = UserScreenRoute,
        icon = Icons.Filled.Home,
        labelRes = R.string.nav_bar_inicio,
    ),
    UserSettings(
        route = SettingsHubRoute,
        icon = Icons.Filled.Settings,
        labelRes = R.string.nav_bar_ajustes,
    ),
    ShelterAnimals(
        route = ShelterAnimalsRoute,
        icon = Icons.Filled.Home,
        labelRes = R.string.nav_bar_mis_animales,
    ),
    ShelterSettings(
        route = ShelterSettingsTabRoute,
        icon = Icons.Filled.Settings,
        labelRes = R.string.nav_bar_ajustes,
    );

    companion object {
        val userTabs: List<NavBarTab> = listOf(UserHome, UserSettings)
        val shelterTabs: List<NavBarTab> = listOf(ShelterAnimals, ShelterSettings)
    }
}
