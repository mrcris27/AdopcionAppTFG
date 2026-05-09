package com.example.adopciontfg.app.ui.screens.user_home_screen

import androidx.lifecycle.ViewModel
import com.example.adopciontfg.data.Shelter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class UserHomeUiState(
    val query: String = "",
    val selectedTab: Int = 0,
    val tabs: List<String> = listOf("Lista", "Mapa"),
    val shelters: List<Shelter> = sampleShelters,
    val filteredShelters: List<Shelter> = sampleShelters
)

private val sampleShelters = listOf(
    Shelter("1", "Protectora 1", 40.4168, -3.7038),
    Shelter("2", "Protectora 2", 40.45, -3.70),
    Shelter("3", "Protectora 3", 40.40, -3.65)
)

@HiltViewModel
class UserHomeViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(UserHomeUiState())
    val uiState: StateFlow<UserHomeUiState> = _uiState.asStateFlow()

    fun onQueryChange(query: String) {
        _uiState.update { state ->
            state.copy(
                query = query,
                filteredShelters = state.shelters.filter { shelter ->
                    query.isBlank() || shelter.name.contains(query, ignoreCase = true)
                }
            )
        }
    }

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }
}
