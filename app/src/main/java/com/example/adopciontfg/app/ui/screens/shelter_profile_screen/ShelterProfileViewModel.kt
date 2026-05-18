package com.example.adopciontfg.app.ui.screens.shelter_profile_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.adopciontfg.data.local.entity.AnimalEntity
import com.example.adopciontfg.data.local.entity.ShelterEntity
import com.example.adopciontfg.data.repository.AnimalRepository
import com.example.adopciontfg.data.repository.ShelterRepository
import com.example.adopciontfg.model.Characteristic
import com.example.adopciontfg.model.Species
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ShelterProfileUiState(
    val shelter: ShelterEntity? = null,
    val animals: List<AnimalEntity> = emptyList(),
    val filteredAnimals: List<AnimalEntity> = emptyList(),
    val query: String = "",
    val selectedSpecies: Species? = null,
    val selectedSex: Boolean? = null,
    val selectedCharacteristics: Set<Characteristic> = emptySet(),
    val isLoading: Boolean = true,
    val isPetsLoading: Boolean = true,
) {
    val hasActiveFilters: Boolean
        get() = selectedSpecies != null || selectedSex != null || selectedCharacteristics.isNotEmpty()

    val isFiltering: Boolean
        get() = query.isNotBlank() || hasActiveFilters
}

@HiltViewModel
class ShelterProfileViewModel @Inject constructor(
    private val shelterRepository: ShelterRepository,
    private val animalRepository: AnimalRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShelterProfileUiState())
    val uiState: StateFlow<ShelterProfileUiState> = _uiState.asStateFlow()

    fun loadShelter(shelterId: String) {
        viewModelScope.launch {
            shelterRepository.getShelterById(shelterId).asFlow().collect { shelter ->
                _uiState.update {
                    it.copy(
                        shelter = shelter,
                        isLoading = false,
                    )
                }
            }
        }
        viewModelScope.launch {
            animalRepository.getAnimalsByShelter(shelterId).asFlow().collect { animals ->
                _uiState.update { state ->
                    val loadedAnimals = animals.orEmpty()
                    state.copy(
                        animals = loadedAnimals,
                        filteredAnimals = filterAnimals(
                            animals = loadedAnimals,
                            query = state.query,
                            selectedSpecies = state.selectedSpecies,
                            selectedSex = state.selectedSex,
                            selectedCharacteristics = state.selectedCharacteristics,
                        ),
                        isPetsLoading = false,
                    )
                }
            }
        }
    }

    fun onQueryChange(query: String) {
        _uiState.update { state ->
            state.copy(
                query = query,
                filteredAnimals = state.filteredWith(query = query),
            )
        }
    }

    fun onSpeciesFilterChange(species: Species?) {
        _uiState.update { state ->
            state.copy(
                selectedSpecies = species,
                filteredAnimals = state.filteredWith(selectedSpecies = species),
            )
        }
    }

    fun onSexFilterChange(sex: Boolean?) {
        _uiState.update { state ->
            state.copy(
                selectedSex = sex,
                filteredAnimals = state.filteredWith(selectedSex = sex),
            )
        }
    }

    fun onCharacteristicToggle(characteristic: Characteristic) {
        _uiState.update { state ->
            val selectedCharacteristics = if (characteristic in state.selectedCharacteristics) {
                state.selectedCharacteristics - characteristic
            } else {
                state.selectedCharacteristics + characteristic
            }
            state.copy(
                selectedCharacteristics = selectedCharacteristics,
                filteredAnimals = state.filteredWith(selectedCharacteristics = selectedCharacteristics),
            )
        }
    }

    fun clearFilters() {
        _uiState.update { state ->
            state.copy(
                selectedSpecies = null,
                selectedSex = null,
                selectedCharacteristics = emptySet(),
                filteredAnimals = state.filteredWith(
                    selectedSpecies = null,
                    selectedSex = null,
                    selectedCharacteristics = emptySet(),
                ),
            )
        }
    }

    private fun ShelterProfileUiState.filteredWith(
        query: String = this.query,
        selectedSpecies: Species? = this.selectedSpecies,
        selectedSex: Boolean? = this.selectedSex,
        selectedCharacteristics: Set<Characteristic> = this.selectedCharacteristics,
    ): List<AnimalEntity> = filterAnimals(
        animals = animals,
        query = query,
        selectedSpecies = selectedSpecies,
        selectedSex = selectedSex,
        selectedCharacteristics = selectedCharacteristics,
    )

    private fun filterAnimals(
        animals: List<AnimalEntity>,
        query: String,
        selectedSpecies: Species?,
        selectedSex: Boolean?,
        selectedCharacteristics: Set<Characteristic>,
    ): List<AnimalEntity> =
        animals.filter { animal ->
            val matchesQuery = query.isBlank() ||
                animal.name.orEmpty().contains(query, ignoreCase = true)
            val matchesSpecies = selectedSpecies == null || animal.species == selectedSpecies
            val matchesSex = selectedSex == null || animal.isSex == selectedSex
            val matchesCharacteristics = selectedCharacteristics.isEmpty() ||
                animal.characteristics.orEmpty().containsAll(selectedCharacteristics)

            matchesQuery && matchesSpecies && matchesSex && matchesCharacteristics
        }
}
