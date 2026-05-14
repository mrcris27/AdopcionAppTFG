package com.example.adopciontfg.app.ui.screens.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.adopciontfg.R
import com.example.adopciontfg.app.ui.screens.components.bottom_tab.BottomTab

@Composable
fun BottomNavBar(
    selectedTab: BottomTab,
    onSelect: (BottomTab) -> Unit
) {
    val items = listOf(
        BottomTab.Home,
        BottomTab.Settings
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        tonalElevation = 0.dp,
        modifier = Modifier
    ) {
        items.forEach { tab ->
            val selected = selectedTab == tab
            NavigationBarItem(
                selected = selected,
                onClick = { onSelect(tab) },
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = when (tab) {
                            BottomTab.Home -> stringResource(R.string.nav_bar_inicio)
                            BottomTab.Settings -> stringResource(R.string.nav_bar_ajustes)
                        }
                    )
                },
                label = {},
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                )
            )
        }
    }
}
