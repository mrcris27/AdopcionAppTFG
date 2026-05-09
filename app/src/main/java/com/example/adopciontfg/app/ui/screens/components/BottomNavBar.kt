package com.example.adopciontfg.app.ui.screens.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.adopciontfg.app.ui.screens.components.bottom_tab.BottomTab

@Composable
fun BottomNavBar(
    selectedTab: BottomTab,
    onSelect: (BottomTab) -> Unit
) {

    val items = listOf(
        BottomTab.Home,
        BottomTab.Pets,
        BottomTab.Settings
    )

    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            items.forEach { tab ->

                val isSelected = selectedTab == tab

                val tint by animateColorAsState(
                    targetValue = if (isSelected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    label = "iconTint"
                )

                val bg by animateColorAsState(
                    targetValue = if (isSelected)
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    else
                        Color.Transparent,
                    label = "bgTint"
                )

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(bg, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = { onSelect(tab) }) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = null,
                            tint = tint
                        )
                    }
                }
            }
        }
    }
}