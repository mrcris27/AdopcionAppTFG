package com.example.adopciontfg.app.ui.screens.shelter.animals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.adopciontfg.BuildConfig
import com.example.adopciontfg.app.ui.state.DataRefreshError
import com.example.adopciontfg.app.ui.state.awaitDataRefresh
import com.example.adopciontfg.app.ui.state.toDataRefreshError
import com.example.adopciontfg.data.local.entity.AnimalEntity
import com.example.adopciontfg.data.repository.AnimalRepository
import com.example.adopciontfg.data.sampleAnimals
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
    val isRefreshing: Boolean = false,
    val refreshError: DataRefreshError? = null,
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
        val shelterId = auth.currentUser?.uid ?: DEBUG_SAMPLE_SHELTER_ID.takeIf { BuildConfig.DEBUG }
        val shelterName = auth.currentUser?.displayName.orEmpty().ifBlank {
            if (BuildConfig.DEBUG) DEBUG_SAMPLE_SHELTER_NAME else ""
        }
        _uiState.update { it.copy(shelterId = shelterId, shelterName = shelterName) }

        viewModelScope.launch {
            settingsRepository.shelterSettings().collectLatest { settings ->
                _uiState.update { it.copy(adoptionFormUrl = settings.adoptionFormUrl) }
            }
        }

        if (shelterId != null) {
            viewModelScope.launch {
                animalRepository.getAnimalsByShelter(shelterId).asFlow().collect { loaded ->
                    val animals = loaded.orEmpty().ifEmpty {
                        debugSampleAnimalsFor(shelterId)
                    }
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
            refreshAnimals(showRefreshIndicator = false)
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

    private fun debugSampleAnimalsFor(shelterId: String): List<AnimalEntity> {
        if (!BuildConfig.DEBUG) return emptyList()

        return sampleAnimals
            .filter { it.shelterId == DEBUG_SAMPLE_SHELTER_ID }
            .map { animal -> animal.withShelterId(shelterId) }
    }

    private fun AnimalEntity.withShelterId(shelterId: String): AnimalEntity {
        return AnimalEntity(
            id,
            name,
            isSex,
            mainPhoto,
            photos,
            birthDate,
            description,
            species,
            characteristics,
            shelterId,
        )
    }

    private companion object {
        const val DEBUG_SAMPLE_SHELTER_ID = "1"
        const val DEBUG_SAMPLE_SHELTER_NAME = "Protectora de prueba"
    }
}
