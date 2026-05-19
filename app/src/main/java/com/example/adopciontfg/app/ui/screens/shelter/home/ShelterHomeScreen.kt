package com.example.adopciontfg.app.ui.screens.shelter.home

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.adopciontfg.app.ui.screens.components.BottomNavBar
import com.example.adopciontfg.app.ui.screens.components.bottom_tab.NavBarTab
import com.example.adopciontfg.app.ui.screens.shelter.home.navigation.ShelterAddPetRoute
import com.example.adopciontfg.app.ui.screens.shelter.home.navigation.ShelterMainNavHost
import com.example.adopciontfg.app.ui.screens.shelter.home.navigation.ShelterSettingsTabRoute

@Composable
fun ShelterHomeScreen(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val destination = navBackStackEntry?.destination

    val showBottomBar = destination?.hasRoute<ShelterAddPetRoute>() == true ||
        destination?.hasRoute<ShelterSettingsTabRoute>() == true

    val currentTab = when {
        destination?.hasRoute<ShelterSettingsTabRoute>() == true -> NavBarTab.ShelterSettings
        else -> NavBarTab.ShelterAddPet
    }

    Scaffold(
        modifier = modifier,
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
                    tabs = NavBarTab.shelterTabs,
                )
            }
        },
    ) { padding ->
        ShelterMainNavHost(
            navController = navController,
            onLogout = onLogout,
            modifier = Modifier.padding(padding),
        )
    }
}
