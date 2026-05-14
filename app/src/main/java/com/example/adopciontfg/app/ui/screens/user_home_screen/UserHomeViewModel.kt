package com.example.adopciontfg.app.ui.screens.user_home_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adopciontfg.data.Shelter
import com.example.adopciontfg.data.sampleShelters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UserHomeUiState(
    val query: String = "",
    val selectedTab: Int = 0,
    val tabs: List<String> = listOf("Lista", "Mapa"),
    val isLoadingShelters: Boolean = true,
    val shelters: List<Shelter> = emptyList(),
    val filteredShelters: List<Shelter> = emptyList()
)

@HiltViewModel
class UserHomeViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(UserHomeUiState())
    val uiState: StateFlow<UserHomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            delay(INITIAL_LOAD_DELAY_MS)
            _uiState.update { state ->
                val loaded = sampleShelters
                state.copy(
                    isLoadingShelters = false,
                    shelters = loaded,
                    filteredShelters = loaded.filter { shelter ->
                        state.query.isBlank() ||
                            shelter.name.contains(state.query, ignoreCase = true)
                    }
                )
            }
        }
    }

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

    companion object {
        /** Simula latencia de red hasta conectar un repositorio real. */
        private const val INITIAL_LOAD_DELAY_MS = 450L
    }
}
