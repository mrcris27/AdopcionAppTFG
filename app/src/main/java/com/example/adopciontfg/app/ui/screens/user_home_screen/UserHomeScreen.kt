package com.example.adopciontfg.app.ui.screens.user_home_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.adopciontfg.app.ui.screens.user_home_screen.components.ListCardView
import com.example.adopciontfg.app.ui.screens.user_home_screen.components.ShelterMapView
import com.example.adopciontfg.data.Shelter

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun UserHomeScreen() {

    var query by remember { mutableStateOf("") }
    val selectedTab = remember { mutableStateOf(0) }

    val tabs = listOf("Lista", "Mapa")

    val shelters = listOf(
        Shelter("Protectora 1", 40.4168, -3.7038),
        Shelter("Protectora 2", 40.45, -3.70),
        Shelter("Protectora 3", 40.40, -3.65)
    )

    Scaffold(
        topBar = {
            HomeTopBar(
                query = query,
                onQueryChange = { query = it },
                tabs = tabs,
                selectedTab = selectedTab.value,
                onTabSelected = { selectedTab.value = it }
            )
        },
        bottomBar = {
            HomeBottomBar()
        }
    ) { innerPadding ->
        HomeContent(
            modifier = Modifier.padding(innerPadding),
            selectedTab = selectedTab.value,
            shelters = shelters
        )
    }
}

/* ---------------- TOP BAR ---------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    tabs: List<String>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Column {
        SearchSection(query, onQueryChange)

        HorizontalDivider()

        TabsSection(
            tabs = tabs,
            selectedTab = selectedTab,
            onTabSelected = onTabSelected
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchSection(
    query: String,
    onQueryChange: (String) -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            SearchBar(query, onQueryChange)
        }
    )
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(50.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(20.dp)
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Icon(Icons.Default.Search, contentDescription = null)

            Spacer(modifier = Modifier.width(8.dp))

            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("Buscar...") },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun TabsSection(
    tabs: List<String>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    SecondaryTabRow(selectedTabIndex = selectedTab) {
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
    shelters: List<Shelter>
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        when (selectedTab) {
            0 -> ListCardView()
            1 -> ShelterMapView(shelters)
        }
    }
}

/* ---------------- BOTTOM BAR ---------------- */

@Composable
fun HomeBottomBar() {
    BottomAppBar {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BottomIcon(Icons.Filled.Home, "Home")
            BottomIcon(Icons.Filled.Pets, "Pets")
            BottomIcon(Icons.Filled.Settings, "Settings")
        }
    }
}

@Composable
fun BottomIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String
) {
    IconButton(onClick = {}) {
        Icon(icon, contentDescription = description)
    }
}