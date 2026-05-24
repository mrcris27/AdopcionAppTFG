package com.example.adopciontfg.app.ui.screens.user.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.adopciontfg.R
import com.example.adopciontfg.data.local.entity.ShelterEntity
import com.example.adopciontfg.data.repository.ShelterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UserHomeUiState(
    val query: String = "",
    val selectedTab: Int = 0,
    val tabs: List<Int> = listOf(R.string.lista, R.string.mapa),
    val isLoadingShelters: Boolean = true,
    val shelters: List<ShelterEntity> = emptyList(),
    val filteredShelters: List<ShelterEntity> = emptyList()
)

@HiltViewModel
class UserHomeViewModel @Inject constructor(
    private val shelterRepository: ShelterRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(UserHomeUiState())
    val uiState: StateFlow<UserHomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            shelterRepository.getAllShelters().asFlow().collect { loaded ->
                _uiState.update { state ->
                    val filtered = loaded.filter { shelter ->
                        state.query.isBlank() ||
                            shelter.name.orEmpty().contains(state.query, ignoreCase = true)
                    }
                    state.copy(
                        isLoadingShelters = false,
                        shelters = loaded,
                        filteredShelters = filtered
                    )
                }
            }
        }
    }

    fun onQueryChange(query: String) {
        _uiState.update { state ->
            state.copy(
                query = query,
                filteredShelters = state.shelters.filter { shelter ->
                    query.isBlank() || shelter.name.orEmpty().contains(query, ignoreCase = true)
                }
            )
        }
    }

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }
}
