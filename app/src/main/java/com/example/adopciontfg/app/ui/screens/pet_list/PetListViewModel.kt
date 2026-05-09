package com.example.adopciontfg.app.ui.screens.pet_list

import androidx.lifecycle.ViewModel
import com.example.adopciontfg.data.Pet
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class PetListUiState(
    val query: String = "",
    val pets: List<Pet> = samplePets,
    val filteredPets: List<Pet> = samplePets
)

private val samplePets = listOf(
    Pet("1", "Max", 3, "Labrador", "Male"),
    Pet("2", "Luna", 2, "Poodle", "Female")
)

@HiltViewModel
class PetListViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(PetListUiState())
    val uiState: StateFlow<PetListUiState> = _uiState.asStateFlow()

    fun onQueryChange(query: String) {
        _uiState.update { state ->
            state.copy(
                query = query,
                filteredPets = state.pets.filter { pet ->
                    query.isBlank() || pet.name.contains(query, ignoreCase = true)
                }
            )
        }
    }
}