package com.example.adopciontfg.app.ui.screens.user.pet_detail

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

data class PetDetailUiState(
    val animal: AnimalEntity? = null,
    val isLoading: Boolean = true,
)

@HiltViewModel
class PetDetailViewModel @Inject constructor(
    private val animalRepository: AnimalRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PetDetailUiState())
    val uiState: StateFlow<PetDetailUiState> = _uiState.asStateFlow()

    fun loadAnimal(animalId: String) {
        viewModelScope.launch {
            animalRepository.getAnimalById(animalId).asFlow().collect { animal ->
                _uiState.update {
                    it.copy(
                        animal = animal,
                        isLoading = false,
                    )
                }
            }
        }
    }
}
