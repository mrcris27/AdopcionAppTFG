package com.example.adopciontfg.app.ui.screens.user_home_screen

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.adopciontfg.app.ui.screens.components.CardViewList
import com.example.adopciontfg.app.ui.screens.components.ListCardView
import com.example.adopciontfg.app.ui.screens.components.SearchBar
import com.example.adopciontfg.app.ui.screens.user_home_screen.components.ShelterMapView
import com.example.adopciontfg.data.Shelter
import com.example.adopciontfg.ui.theme.AdoptionTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHomeScreen(
    navController: NavHostController,
    onDetailClick: (Shelter) -> Unit = {}
) {
    var query by remember { mutableStateOf("") }
    val selectedTab = remember { mutableStateOf(0) }

    val tabs = listOf("Lista", "Mapa")

    val shelters = listOf(
        Shelter("1", "Protectora 1", 40.4168, -3.7038),
        Shelter("2", "Protectora 2", 40.45, -3.70),
        Shelter("3", "Protectora 3", 40.40, -3.65)
    )

    Scaffold { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 8.dp)
                .fillMaxSize()
        ) {

            HomeTopBar(
                query = query,
                onQueryChange = { query = it },
                tabs = tabs,
                selectedTab = selectedTab.value,
                onTabSelected = { selectedTab.value = it }
            )

            HomeContent(
                modifier = Modifier.weight(1f),
                selectedTab = selectedTab.value,
                shelters = shelters,
                onDetailClick = onDetailClick
            )
        }
    }
}

/* ---------------- TOP BAR ---------------- */
@Composable
fun HomeTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    tabs: List<String>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Spacer(modifier = Modifier.height(12.dp))

        SearchBar(
            query = query,
            onQueryChange = onQueryChange
        )

        Spacer(modifier = Modifier.height(12.dp))

        HorizontalDivider(
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        TabsSection(
            tabs = tabs,
            selectedTab = selectedTab,
            onTabSelected = onTabSelected
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}


@Composable
fun TabsSection(
    tabs: List<String>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    SecondaryTabRow(
        selectedTabIndex = selectedTab,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                text = { Text(title) }
            )
        }
    }
}

/* ---------------- CONTENT ---------------- */
@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    selectedTab: Int,
    shelters: List<Shelter>,
    onDetailClick: (Shelter) -> Unit

) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Crossfade(targetState = selectedTab) { tab ->
            when (tab) {
                0 -> ListCardView(
                    items = shelters,
                    onItemClick = onDetailClick,
                    itemContent = { shelter, onClick ->
                        CardViewList(
                            name = shelter.name,
                            onClick = onClick
                        )
                    }
                )

                1 -> ShelterMapView(shelters)
            }
        }
    }
}


@Composable
fun BottomIcon(
    icon: ImageVector,
    description: String
) {
    IconButton(onClick = {}) {
        Icon(icon, contentDescription = description)
    }
}

@Preview(showBackground = true)
@Composable
fun UserHomeScreenPreview() {
    AdoptionTheme {
        UserHomeScreen(
            navController = NavHostController(LocalContext.current)
        )
    }
}