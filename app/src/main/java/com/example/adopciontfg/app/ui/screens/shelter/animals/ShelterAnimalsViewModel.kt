package com.example.adopciontfg.app.ui.screens.shelter.animals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.adopciontfg.data.local.entity.AnimalEntity
import com.example.adopciontfg.data.repository.AnimalRepository
import com.example.adopciontfg.model.AnimalStatus
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ShelterAnimalFilter {
    ALL,
    AVAILABLE,
    RESERVED,
    ADOPTED,
}

data class ShelterAnimalsUiState(
    val shelterId: String? = null,
    val shelterName: String = "",
    val isLoading: Boolean = true,
    val query: String = "",
    val statusFilter: ShelterAnimalFilter = ShelterAnimalFilter.ALL,
    val animals: List<AnimalEntity> = emptyList(),
    val filteredAnimals: List<AnimalEntity> = emptyList(),
    val availableCount: Int = 0,
    val reservedCount: Int = 0,
    val adoptedCount: Int = 0,
)

@HiltViewModel
class ShelterAnimalsViewModel @Inject constructor(
    private val animalRepository: AnimalRepository,
) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val _uiState = MutableStateFlow(ShelterAnimalsUiState())
    val uiState: StateFlow<ShelterAnimalsUiState> = _uiState.asStateFlow()

    init {
        val shelterId = auth.currentUser?.uid
        val shelterName = auth.currentUser?.displayName.orEmpty()
        _uiState.update { it.copy(shelterId = shelterId, shelterName = shelterName) }

        if (shelterId != null) {
            viewModelScope.launch {
                animalRepository.getAnimalsByShelter(shelterId).asFlow().collect { loaded ->
                val animals = loaded.orEmpty()
                _uiState.update { state ->
                    val filtered = applyFilters(animals, state.query, state.statusFilter)
                    state.copy(
                        isLoading = false,
                        animals = animals,
                        filteredAnimals = filtered,
                        availableCount = animals.count { it.status == AnimalStatus.AVAILABLE },
                        reservedCount = animals.count { it.status == AnimalStatus.RESERVED },
                        adoptedCount = animals.count { it.status == AnimalStatus.ADOPTED },
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
                filteredAnimals = applyFilters(state.animals, query, state.statusFilter),
            )
        }
    }

    fun onStatusFilterChange(filter: ShelterAnimalFilter) {
        _uiState.update { state ->
            state.copy(
                statusFilter = filter,
                filteredAnimals = applyFilters(state.animals, state.query, filter),
            )
        }
    }

    fun updateAnimalStatus(animalId: String, status: AnimalStatus) {
        val animal = _uiState.value.animals.find { it.id == animalId } ?: return
        animal.status = status
        animalRepository.updateAnimal(animal)
    }

    private fun applyFilters(
        animals: List<AnimalEntity>,
        query: String,
        filter: ShelterAnimalFilter,
    ): List<AnimalEntity> {
        return animals
            .filter { animal ->
                when (filter) {
                    ShelterAnimalFilter.ALL -> true
                    ShelterAnimalFilter.AVAILABLE -> animal.status == AnimalStatus.AVAILABLE
                    ShelterAnimalFilter.RESERVED -> animal.status == AnimalStatus.RESERVED
                    ShelterAnimalFilter.ADOPTED -> animal.status == AnimalStatus.ADOPTED
                }
            }
            .filter { animal ->
                query.isBlank() || animal.name.orEmpty().contains(query, ignoreCase = true)
            }
            .sortedWith(
                compareBy<AnimalEntity> { it.status == AnimalStatus.ADOPTED }
                    .thenBy { it.name.orEmpty() },
            )
    }
}
