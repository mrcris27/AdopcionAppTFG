package com.example.adopciontfg.app.ui.screens.user_home_screen

import androidx.lifecycle.ViewModel
import com.example.adopciontfg.data.Shelter
import com.example.adopciontfg.data.sampleShelters
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
