package com.example.adopciontfg.app.ui.screens.pet_list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.adopciontfg.app.ui.screens.components.CardViewList
import com.example.adopciontfg.app.ui.screens.components.ListCardView
import com.example.adopciontfg.app.ui.screens.components.SearchSection
import com.example.adopciontfg.data.Pet
import com.example.adopciontfg.ui.theme.AdoptionTheme

@Composable
fun PetListScreen(onDetailClick: (String) -> Unit ) {
    var query by remember { mutableStateOf("") }

    val pets = listOf(
        Pet("1","Max", 3, "Labrador", "Male"),
        Pet("2","Luna", 2, "Poodle", "Female")
    )


    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            HomeTopBar(
                query = query,
                onQueryChange = { query = it },
            )
        }
    ) { innerPadding ->
        PetContent(
            modifier = Modifier.padding(innerPadding),
            pets = pets,
            onDetailClick = onDetailClick
        )
    }
}

@Composable
fun PetContent(
    modifier: Modifier = Modifier,
    pets: List<Pet>,
    onDetailClick: (String) -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        ListCardView(
            items = pets,
            onItemClick = { pet ->
                onDetailClick(pet.id)
            },
            itemContent = { pet, onClick ->
                CardViewList(
                    name = pet.name,
                    onClick = onClick

                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
) {
    Column {
        SearchSection(query, onQueryChange)
        HorizontalDivider()
    }
}

@Preview
@Composable
fun PetListScreenPreview() {
    AdoptionTheme {
        PetListScreen(
            onDetailClick = {}
        )
    }
}