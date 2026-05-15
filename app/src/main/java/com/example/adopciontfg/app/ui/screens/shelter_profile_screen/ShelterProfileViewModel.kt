package com.example.adopciontfg.app.ui.screens.shelter_profile_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.adopciontfg.data.local.entity.AnimalEntity
import com.example.adopciontfg.data.local.entity.ShelterEntity
import com.example.adopciontfg.data.repository.AnimalRepository
import com.example.adopciontfg.data.repository.ShelterRepository
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
    val isLoading: Boolean = true,
    val isPetsLoading: Boolean = true,
)

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
                _uiState.update {
                    it.copy(
                        animals = animals.orEmpty(),
                        isPetsLoading = false,
                    )
                }
            }
        }
    }
}
