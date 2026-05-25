package com.example.adopciontfg.app.ui.screens.user.pet_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.adopciontfg.app.ui.state.DataRefreshError
import com.example.adopciontfg.app.ui.state.awaitDataRefresh
import com.example.adopciontfg.app.ui.state.toDataRefreshError
import com.example.adopciontfg.data.local.entity.AnimalEntity
import com.example.adopciontfg.data.repository.AnimalRepository
import com.example.adopciontfg.data.repository.ShelterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PetDetailUiState(
    val animal: AnimalEntity? = null,
    val adoptionFormUrl: String = "",
    val isAdoptionFormLoading: Boolean = false,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val refreshError: DataRefreshError? = null,
)

@HiltViewModel
class PetDetailViewModel @Inject constructor(
    private val animalRepository: AnimalRepository,
    private val shelterRepository: ShelterRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PetDetailUiState())
    val uiState: StateFlow<PetDetailUiState> = _uiState.asStateFlow()
    private var currentAnimalId: String? = null
    private var shelterObservationJob: Job? = null

    fun loadAnimal(animalId: String) {
        currentAnimalId = animalId
        viewModelScope.launch {
            animalRepository.getAnimalById(animalId).asFlow().collectLatest { animal ->
                _uiState.update {
                    it.copy(
                        animal = animal,
                        isLoading = false,
                        adoptionFormUrl = "",
                    )
                }
                observeShelterAdoptionForm(animal?.shelterId)
            }
        }
        refreshAnimal(showRefreshIndicator = false)
    }

    private fun observeShelterAdoptionForm(shelterId: String?) {
        shelterObservationJob?.cancel()
        if (shelterId.isNullOrBlank()) {
            _uiState.update { it.copy(isAdoptionFormLoading = false) }
            return
        }

        shelterObservationJob = viewModelScope.launch {
            _uiState.update { it.copy(isAdoptionFormLoading = true) }
            shelterRepository.getShelterById(shelterId).asFlow().collectLatest { shelter ->
                _uiState.update { state ->
                    state.copy(
                        adoptionFormUrl = shelter?.adoptionFormUrl.orEmpty(),
                        isAdoptionFormLoading = false,
                    )
                }
            }
        }
    }

    fun refreshAnimal() {
        refreshAnimal(showRefreshIndicator = true)
    }

    private fun refreshAnimal(showRefreshIndicator: Boolean) {
        val animalId = currentAnimalId ?: return
        if (_uiState.value.isRefreshing) return

        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = showRefreshIndicator, refreshError = null) }
            try {
                awaitDataRefresh { onSuccess, onFailure ->
                    animalRepository.refreshAnimalById(
                        animalId,
                        { onSuccess() },
                        { exception -> onFailure(exception) },
                    )
                }

                val shelterId = _uiState.value.animal?.shelterId
                if (!shelterId.isNullOrBlank()) {
                    awaitDataRefresh { onSuccess, onFailure ->
                        shelterRepository.refreshShelterById(
                            shelterId,
                            { onSuccess() },
                            { exception -> onFailure(exception) },
                        )
                    }
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
