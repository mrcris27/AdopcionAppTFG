package com.example.adopciontfg.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.adopciontfg.app.ui.screens.components.BottomNavBar
import com.example.adopciontfg.app.ui.screens.components.bottom_tab.BottomTab
import com.example.adopciontfg.app.ui.screens.pet_list.navigation.PetListRoute
import com.example.adopciontfg.app.ui.screens.user_home_screen.navigation.UserScreenRoute

@Composable
fun AppScaffold(navController: NavHostController) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val currentTab = when (currentRoute) {
        UserScreenRoute::class.qualifiedName -> BottomTab.Home
        PetListRoute::class.qualifiedName -> BottomTab.Pets
        else -> BottomTab.Home
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedTab = currentTab,
                onSelect = { tab ->
                    navController.navigate(tab.route)
                }
            )
        }
    ) { padding ->

        ScreensNavHost(
            navController = navController,
            modifier = Modifier.padding(padding)
        )
    }
}
