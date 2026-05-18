package com.example.adopciontfg.app.ui.screens.pet_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
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
}
