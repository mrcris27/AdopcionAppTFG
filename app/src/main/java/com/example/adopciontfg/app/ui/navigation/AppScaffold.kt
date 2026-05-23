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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.adopciontfg.app.ui.screens.components.BottomNavBar
import com.example.adopciontfg.app.ui.screens.components.bottom_tab.NavBarTab

@Composable
fun AppScaffold(onLogout: () -> Unit) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val destination = navBackStackEntry?.destination

    val showBottomBar = destination.showsUserBottomBar()

    val currentTab = when {
        destination.isUserSettingsRoute() -> NavBarTab.UserSettings
        else -> NavBarTab.UserHome
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing.only(
            WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom,
        ),
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    selectedTab = currentTab,
                    onSelect = { tab ->
                        navController.navigate(tab.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) { padding ->
        MainNavHost(
            navController = navController,
            onLogout = onLogout,
            modifier = Modifier.padding(padding),
        )
    }
}
