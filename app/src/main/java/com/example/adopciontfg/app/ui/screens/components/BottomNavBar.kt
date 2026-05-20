package com.example.adopciontfg.app.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.adopciontfg.app.ui.screens.components.bottom_tab.NavBarTab
import com.example.adopciontfg.ui.theme.Dimens
import com.example.adopciontfg.ui.theme.elevatedSurface
import com.example.adopciontfg.ui.theme.subtleDivider

private val DefaultUserTabs = listOf(NavBarTab.UserHome, NavBarTab.UserSettings)

@Composable
fun BottomNavBar(
    selectedTab: NavBarTab,
    onSelect: (NavBarTab) -> Unit,
    tabs: List<NavBarTab> = DefaultUserTabs,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.elevatedSurface(),
        shadowElevation = 8.dp,
        tonalElevation = 2.dp,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.subtleDivider(),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .height(Dimens.bottomBarHeight)
                    .padding(horizontal = Dimens.spacingSm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                tabs.forEach { tab ->
                    key(tab) {
                        BottomNavDestination(
                            tab = tab,
                            selected = selectedTab == tab,
                            onClick = { onSelect(tab) },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomNavDestination(
    tab: NavBarTab,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val label = stringResource(tab.labelRes)
    val interactionSource = remember { MutableInteractionSource() }
    val pillColor = if (selected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        Color.Transparent
    }
    val contentColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(
                    bounded = true,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.22f),
                ),
                role = Role.Tab,
                onClickLabel = label,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(pillColor)
                .padding(horizontal = Dimens.spacingXl, vertical = Dimens.spacingSm),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Icon(
                imageVector = tab.icon,
                contentDescription = label,
                modifier = Modifier.size(22.dp),
                tint = contentColor,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            )
        }
    }
}
