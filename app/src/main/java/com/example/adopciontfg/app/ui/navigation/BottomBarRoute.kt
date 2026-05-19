package com.example.adopciontfg.app.ui.navigation

import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import com.example.adopciontfg.app.ui.screens.user.home.navigation.UserScreenRoute
import com.example.adopciontfg.app.ui.screens.user.settings.navigation.SettingsHubRoute

internal fun NavDestination?.isUserHomeRoute(): Boolean {
    if (this == null) return false
    return hasRoute<UserScreenRoute>() ||
        route == UserScreenRoute::class.qualifiedName
}

internal fun NavDestination?.isUserSettingsHubRoute(): Boolean {
    if (this == null) return false
    return hasRoute<SettingsHubRoute>() ||
        route == SettingsHubRoute::class.qualifiedName
}

internal fun NavDestination?.showsUserBottomBar(): Boolean =
    isUserHomeRoute() || isUserSettingsHubRoute()
