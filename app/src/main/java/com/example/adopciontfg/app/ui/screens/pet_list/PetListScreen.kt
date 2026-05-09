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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.adopciontfg.app.ui.screens.components.CardViewList
import com.example.adopciontfg.app.ui.screens.components.ListCardView
import com.example.adopciontfg.app.ui.screens.components.SearchBar
import com.example.adopciontfg.data.Pet
import com.example.adopciontfg.ui.theme.AdoptionTheme

@Composable
fun PetListScreen(
    onDetailClick: (String) -> Unit,
    viewModel: PetListViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            HomeTopBar(
                query = uiState.value.query,
                onQueryChange = viewModel::onQueryChange
            )
        }
    ) { innerPadding ->
        PetContent(
            modifier = Modifier.padding(innerPadding),
            pets = uiState.value.filteredPets,
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
        SearchBar(query, onQueryChange)
        HorizontalDivider()
    }
}

@Preview
@Composable
fun PetListScreenPreview() {
    AdoptionTheme {
        PetListScreen(
            onDetailClick = {},
            viewModel = PetListViewModel()
        )
    }
}