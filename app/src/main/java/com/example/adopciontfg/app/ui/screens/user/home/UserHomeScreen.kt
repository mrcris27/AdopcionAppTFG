package com.example.adopciontfg.app.ui.screens.user.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.adopciontfg.R
import com.example.adopciontfg.app.ui.components.skeleton.MapAreaSkeleton
import com.example.adopciontfg.app.ui.components.skeleton.ShelterCardListSkeleton
import com.example.adopciontfg.app.ui.screens.components.CardViewList
import com.example.adopciontfg.app.ui.screens.components.DataRefreshErrorDialog
import com.example.adopciontfg.app.ui.screens.components.ListCardView
import com.example.adopciontfg.app.ui.screens.components.SearchBar
import com.example.adopciontfg.app.ui.screens.user.home.components.ShelterMapView
import com.example.adopciontfg.data.local.entity.ShelterEntity
import com.example.adopciontfg.ui.theme.AdoptionTheme
import com.example.adopciontfg.ui.theme.Dimens
import com.example.adopciontfg.ui.theme.subtleDivider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHomeScreen(
    modifier: Modifier = Modifier,
    onDetailClick: (ShelterEntity) -> Unit = {},
    viewModel: UserHomeViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    UserHomeScreenBody(
        modifier = modifier,
        uiState = uiState.value,
        onQueryChange = viewModel::onQueryChange,
        onTabSelected = viewModel::onTabSelected,
        onRefresh = viewModel::refreshShelters,
        onRefreshErrorDismiss = viewModel::dismissRefreshError,
        onDetailClick = onDetailClick
    )
}

@Composable
fun UserHomeScreenBody(
    modifier: Modifier = Modifier,
    uiState: UserHomeUiState,
    onQueryChange: (String) -> Unit,
    onTabSelected: (Int) -> Unit,
    onRefresh: () -> Unit,
    onRefreshErrorDismiss: () -> Unit,
    onDetailClick: (ShelterEntity) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.spacingSm)
        ) {
            if (uiState.selectedTab == 0) {
                SearchBar(
                    query = uiState.query,
                    onQueryChange = onQueryChange,
                    modifier = Modifier.padding(vertical = Dimens.spacingSm)
                )
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.subtleDivider()
            )

            TabsSection(
                tabs = uiState.tabs,
                selectedTab = uiState.selectedTab,
                onTabSelected = onTabSelected
            )
        }

        HomeContent(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            selectedTab = uiState.selectedTab,
            isLoadingShelters = uiState.isLoadingShelters,
            isRefreshing = uiState.isRefreshing,
            shelters = uiState.shelters,
            filteredShelters = uiState.filteredShelters,
            onRefresh = onRefresh,
            onDetailClick = onDetailClick
        )
    }

    DataRefreshErrorDialog(
        error = uiState.refreshError,
        onDismiss = onRefreshErrorDismiss,
        onRetry = onRefresh,
    )
}

@Composable
fun TabsSection(
    tabs: List<Int>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    SecondaryTabRow(
        selectedTabIndex = selectedTab,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.primary,
        divider = {}
    ) {
        tabs.forEachIndexed { index, titleRes ->
            Tab(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = stringResource(titleRes),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (selectedTab == index) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            )
        }
    }
}

private const val TAB_ANIMATION_MS = 280

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    selectedTab: Int,
    isLoadingShelters: Boolean,
    isRefreshing: Boolean,
    shelters: List<ShelterEntity>,
    filteredShelters: List<ShelterEntity>,
    onRefresh: () -> Unit,
    onDetailClick: (ShelterEntity) -> Unit
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier
            .fillMaxSize()
            .clipToBounds(),
    ) {
        AnimatedContent(
            targetState = selectedTab,
            modifier = Modifier.fillMaxSize(),
            transitionSpec = {
                val forward = targetState > initialState
                (slideInHorizontally(
                    animationSpec = tween(TAB_ANIMATION_MS),
                    initialOffsetX = { width -> if (forward) width else -width }
                ) + fadeIn(tween(TAB_ANIMATION_MS)))
                    .togetherWith(
                        slideOutHorizontally(
                            animationSpec = tween(TAB_ANIMATION_MS),
                            targetOffsetX = { width -> if (forward) -width else width }
                        ) + fadeOut(tween(TAB_ANIMATION_MS))
                    )
            },
            label = "home_tab_content",
        ) { tab ->
            when (tab) {
                0 -> HomeListTab(
                    isLoading = isLoadingShelters,
                    shelters = filteredShelters,
                    onDetailClick = onDetailClick
                )
                else -> HomeMapTab(
                    isLoading = isLoadingShelters,
                    shelters = shelters
                )
            }
        }
    }
}

@Composable
private fun HomeListTab(
    isLoading: Boolean,
    shelters: List<ShelterEntity>,
    onDetailClick: (ShelterEntity) -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        if (isLoading) {
            ShelterCardListSkeleton(modifier = Modifier.fillMaxSize())
        } else {
            ListCardView(
                items = shelters,
                onItemClick = onDetailClick,
                itemContent = { shelter, onClick ->
                    CardViewList(
                        name = shelter.name.orEmpty(),
                        photoUri = shelter.profilePicture,
                        placeholderIcon = Icons.Default.AccountCircle,
                        onClick = onClick
                    )
                }
            )
        }
    }
}

@Composable
private fun HomeMapTab(
    isLoading: Boolean,
    shelters: List<ShelterEntity>,
) {
    if (isLoading) {
        MapAreaSkeleton(modifier = Modifier.fillMaxSize())
    } else {
        ShelterMapView(
            shelters = shelters,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun BottomIcon(
    icon: ImageVector,
    description: String
) {
    androidx.compose.material3.IconButton(onClick = {}) {
        androidx.compose.material3.Icon(icon, contentDescription = description)
    }
}

@Preview(showBackground = true)
@Composable
fun UserHomeScreenPreview() {
    AdoptionTheme {
        val shelters = previewShelters()

        UserHomeScreenBody(
            uiState = UserHomeUiState(
                isLoadingShelters = false,
                shelters = shelters,
                filteredShelters = shelters
            ),
            onQueryChange = {},
            onTabSelected = {},
            onRefresh = {},
            onRefreshErrorDismiss = {},
            onDetailClick = {}
        )
    }
}

private fun previewShelters(): List<ShelterEntity> = listOf(
    ShelterEntity(
        "preview-shelter-1",
        "Protectora Norte",
        "B00000001",
        null,
        "contacto@protectoranorte.org",
        "Calle Mayor 1",
        "600000001",
        "https://forms.gle/preview",
    ),
    ShelterEntity(
        "preview-shelter-2",
        "Hogar Animal",
        "B00000002",
        null,
        "info@hogaranimal.org",
        "Avenida Central 8",
        "600000002",
        "https://forms.gle/preview",
    ),
)
