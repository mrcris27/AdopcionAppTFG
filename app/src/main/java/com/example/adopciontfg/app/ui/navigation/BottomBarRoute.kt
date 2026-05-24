package com.example.adopciontfg.app.ui.navigation

import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import com.example.adopciontfg.app.ui.screens.user.home.navigation.UserScreenRoute
import com.example.adopciontfg.app.ui.screens.user.settings.navigation.UserSettingsRoute

internal fun NavDestination?.isUserHomeRoute(): Boolean {
    if (this == null) return false
    return hasRoute<UserScreenRoute>() ||
        route == UserScreenRoute::class.qualifiedName
}

internal fun NavDestination?.isUserSettingsRoute(): Boolean {
    if (this == null) return false
    return hasRoute<UserSettingsRoute>() ||
        route == UserSettingsRoute::class.qualifiedName
}

internal fun NavDestination?.showsUserBottomBar(): Boolean =
    isUserHomeRoute() || isUserSettingsRoute()
