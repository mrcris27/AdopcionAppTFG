package com.example.adopciontfg.app.ui.screens.shelter.animals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.adopciontfg.data.local.entity.AnimalEntity
import com.example.adopciontfg.data.repository.AnimalRepository
import com.example.adopciontfg.domain.settings.SettingsRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ShelterAnimalsUiState(
    val shelterId: String? = null,
    val shelterName: String = "",
    val adoptionFormUrl: String = "",
    val isLoading: Boolean = true,
    val query: String = "",
    val animals: List<AnimalEntity> = emptyList(),
    val filteredAnimals: List<AnimalEntity> = emptyList(),
)

@HiltViewModel
class ShelterAnimalsViewModel @Inject constructor(
    private val animalRepository: AnimalRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val _uiState = MutableStateFlow(ShelterAnimalsUiState())
    val uiState: StateFlow<ShelterAnimalsUiState> = _uiState.asStateFlow()

    init {
        val shelterId = auth.currentUser?.uid
        val shelterName = auth.currentUser?.displayName.orEmpty()
        _uiState.update { it.copy(shelterId = shelterId, shelterName = shelterName) }

        viewModelScope.launch {
            settingsRepository.shelterSettings().collectLatest { settings ->
                _uiState.update { it.copy(adoptionFormUrl = settings.adoptionFormUrl) }
            }
        }

        if (shelterId != null) {
            viewModelScope.launch {
                animalRepository.getAnimalsByShelter(shelterId).asFlow().collect { loaded ->
                    val animals = loaded.orEmpty()
                    _uiState.update { state ->
                        val filtered = applyFilters(animals, state.query)
                        state.copy(
                            isLoading = false,
                            animals = animals,
                            filteredAnimals = filtered,
                        )
                    }
                }
            }
        } else {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onQueryChange(query: String) {
        _uiState.update { state ->
            state.copy(
                query = query,
                filteredAnimals = applyFilters(state.animals, query),
            )
        }
    }

    private fun applyFilters(
        animals: List<AnimalEntity>,
        query: String,
    ): List<AnimalEntity> {
        return animals
            .filter { animal ->
                query.isBlank() || animal.name.orEmpty().contains(query, ignoreCase = true)
            }
            .sortedBy { it.name.orEmpty() }
    }
}
