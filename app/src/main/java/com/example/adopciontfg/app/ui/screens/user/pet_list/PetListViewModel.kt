package com.example.adopciontfg.app.ui.screens.user.pet_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.adopciontfg.app.ui.state.DataRefreshError
import com.example.adopciontfg.app.ui.state.awaitDataRefresh
import com.example.adopciontfg.app.ui.state.toDataRefreshError
import com.example.adopciontfg.data.local.entity.AnimalEntity
import com.example.adopciontfg.data.repository.AnimalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PetListUiState(
    val query: String = "",
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val refreshError: DataRefreshError? = null,
    val animals: List<AnimalEntity> = emptyList(),
    val filteredAnimals: List<AnimalEntity> = emptyList(),
)

@HiltViewModel
class PetListViewModel @Inject constructor(
    private val animalRepository: AnimalRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PetListUiState())
    val uiState: StateFlow<PetListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            animalRepository.getAllAnimals().asFlow().collect { loaded ->
                _uiState.update { state ->
                    val animals = loaded.orEmpty()
                    state.copy(
                        isLoading = false,
                        animals = animals,
                        filteredAnimals = filterByQuery(animals, state.query),
                    )
                }
            }
        }
        refreshAnimals(showRefreshIndicator = false)
    }

    fun onQueryChange(query: String) {
        _uiState.update { state ->
            state.copy(
                query = query,
                filteredAnimals = filterByQuery(state.animals, query),
            )
        }
    }

    private fun filterByQuery(animals: List<AnimalEntity>, query: String): List<AnimalEntity> =
        if (query.isBlank()) {
            animals
        } else {
            animals.filter { animal ->
                animal.name.orEmpty().contains(query, ignoreCase = true)
            }
        }

    fun refreshAnimals() {
        refreshAnimals(showRefreshIndicator = true)
    }

    private fun refreshAnimals(showRefreshIndicator: Boolean) {
        if (_uiState.value.isRefreshing) return

        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = showRefreshIndicator, refreshError = null) }
            try {
                awaitDataRefresh { onSuccess, onFailure ->
                    animalRepository.refreshAllAnimals(
                        { onSuccess() },
                        { exception -> onFailure(exception) },
                    )
                }
            } catch (exception: Exception) {
                _uiState.update { it.copy(refreshError = exception.toDataRefreshError()) }
            } finally {
                if (showRefreshIndicator) {
                    _uiState.update { it.copy(isRefreshing = false) }
                }
            }
        }
    }

    fun dismissRefreshError() {
        _uiState.update { it.copy(refreshError = null) }
    }
}
