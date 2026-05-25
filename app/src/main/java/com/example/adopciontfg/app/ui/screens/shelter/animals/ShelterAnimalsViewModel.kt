package com.example.adopciontfg.app.ui.screens.shelter.animals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.adopciontfg.app.ui.state.DataRefreshError
import com.example.adopciontfg.app.ui.state.awaitDataRefresh
import com.example.adopciontfg.app.ui.state.toDataRefreshError
import com.example.adopciontfg.data.local.entity.AnimalEntity
import com.example.adopciontfg.data.repository.AnimalRepository
import com.example.adopciontfg.domain.settings.SettingsRepository
import com.example.adopciontfg.model.Characteristic
import com.example.adopciontfg.model.Species
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
    val isRefreshing: Boolean = false,
    val refreshError: DataRefreshError? = null,
    val query: String = "",
    val selectedSpecies: Species? = null,
    val selectedSex: Boolean? = null,
    val selectedAdoptionStatus: ShelterAnimalAdoptionStatus? = null,
    val selectedCharacteristics: Set<Characteristic> = emptySet(),
    val animals: List<AnimalEntity> = emptyList(),
    val filteredAnimals: List<AnimalEntity> = emptyList(),
) {
    val hasActiveFilters: Boolean
        get() = selectedSpecies != null ||
            selectedSex != null ||
            selectedAdoptionStatus != null ||
            selectedCharacteristics.isNotEmpty()

    val isFiltering: Boolean
        get() = query.isNotBlank() || hasActiveFilters
}

enum class ShelterAnimalAdoptionStatus {
    AVAILABLE,
    NOT_AVAILABLE,
}

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
                        val filtered = state.filteredWith(animals = animals)
                        state.copy(
                            isLoading = false,
                            animals = animals,
                            filteredAnimals = filtered,
                        )
                    }
                }
            }
            refreshAnimals(showRefreshIndicator = false)
        } else {
            _uiState.update { it.copy(isLoading = false) }
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

    fun onAdoptionStatusFilterChange(status: ShelterAnimalAdoptionStatus?) {
        _uiState.update { state ->
            state.copy(
                selectedAdoptionStatus = status,
                filteredAnimals = state.filteredWith(selectedAdoptionStatus = status),
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
                selectedAdoptionStatus = null,
                selectedCharacteristics = emptySet(),
                filteredAnimals = state.filteredWith(
                    selectedSpecies = null,
                    selectedSex = null,
                    selectedAdoptionStatus = null,
                    selectedCharacteristics = emptySet(),
                ),
            )
        }
    }

    fun refreshAnimals() {
        refreshAnimals(showRefreshIndicator = true)
    }

    private fun refreshAnimals(showRefreshIndicator: Boolean) {
        val shelterId = _uiState.value.shelterId ?: return
        if (_uiState.value.isRefreshing) return

        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = showRefreshIndicator, refreshError = null) }
            try {
                awaitDataRefresh { onSuccess, onFailure ->
                    animalRepository.refreshAnimalsByShelter(
                        shelterId,
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

    private fun ShelterAnimalsUiState.filteredWith(
        animals: List<AnimalEntity> = this.animals,
        query: String = this.query,
        selectedSpecies: Species? = this.selectedSpecies,
        selectedSex: Boolean? = this.selectedSex,
        selectedAdoptionStatus: ShelterAnimalAdoptionStatus? = this.selectedAdoptionStatus,
        selectedCharacteristics: Set<Characteristic> = this.selectedCharacteristics,
    ): List<AnimalEntity> {
        return animals
            .filter { animal ->
                val matchesQuery = query.isBlank() ||
                    animal.name.orEmpty().contains(query, ignoreCase = true)
                val matchesSpecies = selectedSpecies == null || animal.species == selectedSpecies
                val matchesSex = selectedSex == null || animal.isSex == selectedSex
                val matchesAdoptionStatus = when (selectedAdoptionStatus) {
                    ShelterAnimalAdoptionStatus.AVAILABLE -> animal.isForAdoption
                    ShelterAnimalAdoptionStatus.NOT_AVAILABLE -> !animal.isForAdoption
                    null -> true
                }
                val matchesCharacteristics = selectedCharacteristics.isEmpty() ||
                    animal.characteristics.orEmpty().containsAll(selectedCharacteristics)

                matchesQuery &&
                    matchesSpecies &&
                    matchesSex &&
                    matchesAdoptionStatus &&
                    matchesCharacteristics
            }
            .sortedBy { it.name.orEmpty() }
    }
}
