package com.example.adopciontfg.app.ui.screens.shelter.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.adopciontfg.R
import com.example.adopciontfg.app.ui.screens.components.BottomNavBar
import com.example.adopciontfg.app.ui.screens.components.bottom_tab.NavBarTab
import com.example.adopciontfg.app.ui.screens.shelter.home.navigation.ShelterAnimalsRoute
import com.example.adopciontfg.app.ui.screens.shelter.home.navigation.ShelterMainNavHost
import com.example.adopciontfg.app.ui.screens.shelter.home.navigation.ShelterSettingsTabRoute
import com.example.adopciontfg.ui.theme.AdoptionTheme

@Composable
fun ShelterHomeScreen(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val destination = navBackStackEntry?.destination

    val showBottomBar = destination?.hasRoute<ShelterAnimalsRoute>() == true ||
        destination?.hasRoute<ShelterSettingsTabRoute>() == true

    val currentTab = when {
        destination?.hasRoute<ShelterSettingsTabRoute>() == true -> NavBarTab.ShelterSettings
        else -> NavBarTab.ShelterHome
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

@Preview(showBackground = true, name = "Protectora - navegación")
@Composable
private fun ShelterHomeScreenPreview() {
    AdoptionTheme {
        Scaffold(
            bottomBar = {
                BottomNavBar(
                    selectedTab = NavBarTab.ShelterHome,
                    onSelect = {},
                    tabs = NavBarTab.shelterTabs,
                )
            },
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.shelter_main_view),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
