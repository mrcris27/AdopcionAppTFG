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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox

import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.tooling.preview.Preview

import com.example.adopciontfg.R

import com.example.adopciontfg.app.ui.components.skeleton.ShelterCardListSkeleton

import com.example.adopciontfg.app.ui.screens.components.CardViewList

import com.example.adopciontfg.app.ui.screens.components.DataRefreshErrorDialog

import com.example.adopciontfg.app.ui.screens.components.ListCardView

import com.example.adopciontfg.app.ui.screens.components.SearchSection

import com.example.adopciontfg.app.ui.screens.components.animalListSubtitle

import com.example.adopciontfg.data.local.entity.AnimalEntity

import com.example.adopciontfg.model.Characteristic
import com.example.adopciontfg.model.Species

import com.example.adopciontfg.ui.theme.AdoptionTheme

import com.example.adopciontfg.ui.theme.Dimens
import com.example.adopciontfg.ui.theme.subtleDivider



@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun PetListScreen(

    uiState: PetListUiState,

    onQueryChange: (String) -> Unit,

    onRefresh: () -> Unit,

    onRefreshErrorDismiss: () -> Unit,

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

        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            PetContent(

                modifier = Modifier.fillMaxSize(),

                isLoading = uiState.isLoading,

                animals = uiState.filteredAnimals,

                onDetailClick = onDetailClick,

            )
        }

    }

    DataRefreshErrorDialog(
        error = uiState.refreshError,
        onDismiss = onRefreshErrorDismiss,
        onRetry = onRefresh,
    )

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

                text = stringResource(R.string.sin_animales_disponibles),

                modifier = Modifier.padding(horizontal = Dimens.spacingSm, vertical = Dimens.screenPadding),

                style = MaterialTheme.typography.bodyLarge,

                color = MaterialTheme.colorScheme.onSurfaceVariant,

            )

            else -> ListCardView(

                items = animals,

                onItemClick = { animal -> onDetailClick(animal.id) },

                itemContent = { animal, onClick ->

                    CardViewList(

                        name = animal.name.orEmpty(),

                        subtitle = animalListSubtitle(animal),
                        photoUri = animal.mainPhoto,

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

            text = stringResource(R.string.animales),

            style = MaterialTheme.typography.titleLarge,

            fontWeight = FontWeight.Bold,

            color = MaterialTheme.colorScheme.onBackground,

            modifier = Modifier.padding(

                start = Dimens.spacingSm,

                top = Dimens.spacingMd,

                bottom = Dimens.spacingXs

            )

        )

        SearchSection(query, onQueryChange)

        HorizontalDivider(

            color = MaterialTheme.colorScheme.subtleDivider(),

            modifier = Modifier.padding(horizontal = Dimens.spacingSm)

        )

    }

}

@Preview(showBackground = true)

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

            onRefresh = {},

            onRefreshErrorDismiss = {},

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
        true,
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
        true,
    ),
)



@Preview(showBackground = true, name = "Cargando")

@Composable

fun PetListScreenLoadingPreview() {

    AdoptionTheme {

        PetListScreen(

            uiState = PetListUiState(isLoading = true),

            onQueryChange = {},

            onRefresh = {},

            onRefreshErrorDismiss = {},

            onDetailClick = {},

        )

    }

}