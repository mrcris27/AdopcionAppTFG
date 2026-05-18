package com.example.adopciontfg.app.ui.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.adopciontfg.app.ui.screens.components.BottomNavBar
import com.example.adopciontfg.app.ui.screens.components.bottom_tab.BottomTab
import com.example.adopciontfg.app.ui.screens.settings.navigation.SettingsHubRoute
import com.example.adopciontfg.app.ui.screens.user_home_screen.navigation.UserScreenRoute

@Composable
fun AppScaffold(onLogout: () -> Unit) {

    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        UserScreenRoute::class.qualifiedName,
        SettingsHubRoute::class.qualifiedName
    )

    val currentTab = when (currentRoute) {
        UserScreenRoute::class.qualifiedName -> BottomTab.Home
        SettingsHubRoute::class.qualifiedName -> BottomTab.Settings
        else -> BottomTab.Home
    }

    Scaffold(
        // Sin inset superior aquí: las pantallas con TopAppBar (Scaffold interno) ya lo aplican.
        // Si también reservamos la barra de estado en este padding, queda un hueco en blanco encima del TopAppBar.
        contentWindowInsets = WindowInsets.safeDrawing.only(
            WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
        ),
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    selectedTab = currentTab,
                    onSelect = { tab ->
                        navController.navigate(tab.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    ) { padding ->

        MainNavHost(
            navController = navController,
            onLogout = onLogout,
            modifier = Modifier.padding(padding)
        )
    }
}