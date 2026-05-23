package com.example.adopciontfg.app.ui.screens.user.pet_list



import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.foundation.layout.padding

import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.material3.HorizontalDivider

import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Scaffold

import androidx.compose.material3.Text

import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier

import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.tooling.preview.Preview

import com.example.adopciontfg.app.ui.components.skeleton.ShelterCardListSkeleton

import com.example.adopciontfg.app.ui.screens.components.CardViewList

import com.example.adopciontfg.app.ui.screens.components.ListCardView

import com.example.adopciontfg.app.ui.screens.components.SearchSection

import com.example.adopciontfg.data.local.entity.AnimalEntity

import com.example.adopciontfg.data.local.entity.listSubtitle

import com.example.adopciontfg.model.AnimalStatus
import com.example.adopciontfg.model.Characteristic
import com.example.adopciontfg.model.Species

import com.example.adopciontfg.ui.theme.AdoptionTheme

import com.example.adopciontfg.ui.theme.Dimens
import com.example.adopciontfg.ui.theme.subtleDivider



@Composable

fun PetListScreen(

    uiState: PetListUiState,

    onQueryChange: (String) -> Unit,

    onDetailClick: (String) -> Unit,

) {

    Scaffold(

        containerColor = MaterialTheme.colorScheme.background,

        topBar = {

            HomeTopBar(

                query = uiState.query,

                onQueryChange = onQueryChange,

            )

        }

    ) { innerPadding ->

        PetContent(

            modifier = Modifier.padding(innerPadding),

            isLoading = uiState.isLoading,

            animals = uiState.filteredAnimals,

            onDetailClick = onDetailClick,

        )

    }

}



@Composable

fun PetContent(

    modifier: Modifier = Modifier,

    isLoading: Boolean,

    animals: List<AnimalEntity>,

    onDetailClick: (String) -> Unit,

) {

    Box(

        modifier = modifier.fillMaxSize(),

    ) {

        when {

            isLoading -> ShelterCardListSkeleton(modifier = Modifier.fillMaxSize())

            animals.isEmpty() -> Text(

                text = "No hay animales disponibles.",

                modifier = Modifier.padding(Dimens.screenPadding),

                style = MaterialTheme.typography.bodyLarge,

                color = MaterialTheme.colorScheme.onSurfaceVariant,

            )

            else -> ListCardView(

                items = animals,

                onItemClick = { animal -> onDetailClick(animal.id) },

                itemContent = { animal, onClick ->

                    CardViewList(

                        name = animal.name.orEmpty(),

                        subtitle = animal.listSubtitle(),

                        onClick = onClick,

                    )

                },

            )

        }

    }

}



@OptIn(ExperimentalMaterial3Api::class)

@Composable

fun HomeTopBar(

    query: String,

    onQueryChange: (String) -> Unit,

) {

    Column {

        Text(

            text = "Animales",

            style = MaterialTheme.typography.titleLarge,

            fontWeight = FontWeight.Bold,

            color = MaterialTheme.colorScheme.onBackground,

            modifier = Modifier.padding(

                start = Dimens.spacingXl,

                top = Dimens.spacingMd,

                bottom = Dimens.spacingXs

            )

        )

        SearchSection(query, onQueryChange)

        HorizontalDivider(

            color = MaterialTheme.colorScheme.subtleDivider(),

            modifier = Modifier.padding(horizontal = Dimens.spacingLg)

        )

    }

}

/*

@Preview

@Composable

fun PetListScreenPreview() {

    AdoptionTheme {
        val animals = previewAnimals()

        PetListScreen(

            uiState = PetListUiState(

                isLoading = false,

                animals = animals,

                filteredAnimals = animals,

            ),

            onQueryChange = {},

            onDetailClick = {},

        )

    }

}

private fun previewAnimals(): List<AnimalEntity> = listOf(
    AnimalEntity(
        "preview-animal-1",
        "Luna",
        true,
        null,
        emptyList(),
        0L,
        "Busca una familia tranquila.",
        Species.PERRO,
        listOf(Characteristic.TRANQUILO),
        "preview-shelter",
        AnimalStatus.AVAILABLE,
    ),
    AnimalEntity(
        "preview-animal-2",
        "Milo",
        false,
        null,
        emptyList(),
        0L,
        "Muy jugueton y sociable.",
        Species.GATO,
        listOf(Characteristic.JUGUETON),
        "preview-shelter",
        AnimalStatus.AVAILABLE,
    ),
)



@Preview(showBackground = true, name = "Cargando")

@Composable

fun PetListScreenLoadingPreview() {

    AdoptionTheme {

        PetListScreen(

            uiState = PetListUiState(isLoading = true),

            onQueryChange = {},

            onDetailClick = {},

        )

    }

}

*/