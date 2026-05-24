package com.example.adopciontfg.app.ui.screens.shelter.pet_registration

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.adopciontfg.BuildConfig
import com.example.adopciontfg.R
import com.example.adopciontfg.data.local.entity.AnimalEntity
import com.example.adopciontfg.data.repository.AnimalRepository
import com.example.adopciontfg.data.sampleAnimals
import com.example.adopciontfg.model.Characteristic
import com.example.adopciontfg.model.Species
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PetFormUiState(
    val isEditMode: Boolean = false,
    val animalId: String? = null,
    val isLoading: Boolean = false,
    val name: String = "",
    val isFemale: Boolean = false,
    val mainPhotoUri: Uri? = null,
    val galleryUris: List<Uri> = emptyList(),
    val birthDateMillis: Long = 0L,
    val description: String = "",
    val species: Species? = null,
    val selectedCharacteristics: Set<Characteristic> = emptySet(),
    val saveMessageRes: Int? = null,
    val saveMessage: String? = null,
    val saveSucceeded: Boolean = false,
) {
    val canSave: Boolean =
        name.isNotBlank() && species != null && birthDateMillis > 0L && mainPhotoUri != null
}

@HiltViewModel
class ShelterPetFormViewModel @Inject constructor(
    private val animalRepository: AnimalRepository,
) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(PetFormUiState())
    val uiState: StateFlow<PetFormUiState> = _uiState.asStateFlow()

    fun loadAnimal(id: String?) {
        if (id == null) {
            _uiState.value = PetFormUiState()
            return
        }
        _uiState.update { PetFormUiState(isEditMode = true, animalId = id, isLoading = true) }
        loadAnimalFromRepo(id)
    }

    private fun loadAnimalFromRepo(id: String) {
        viewModelScope.launch {
            animalRepository.getAnimalById(id).asFlow().collect { entity ->
                val animal = entity ?: debugSampleAnimal(id) ?: return@collect
                loadAnimalIntoState(animal)
            }
        }
    }

    private fun loadAnimalIntoState(entity: AnimalEntity) {
        _uiState.update {
            it.copy(
                isLoading = false,
                name = entity.name.orEmpty(),
                isFemale = entity.isSex,
                mainPhotoUri = entity.mainPhoto?.takeIf { uri -> uri.isNotBlank() }?.let(Uri::parse),
                galleryUris = entity.photos.orEmpty()
                    .filter { photo -> photo.isNotBlank() }
                    .map(Uri::parse),
                birthDateMillis = entity.birthDate,
                description = entity.description.orEmpty(),
                species = entity.species,
                selectedCharacteristics = entity.characteristics.orEmpty().toSet(),
            )
        }
    }

    private fun debugSampleAnimal(id: String): AnimalEntity? {
        if (!BuildConfig.DEBUG) return null
        return sampleAnimals.firstOrNull { it.id == id }
    }

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value) }
    fun onSexChange(isFemale: Boolean) = _uiState.update { it.copy(isFemale = isFemale) }
    fun onMainPhotoChange(uri: Uri?) = _uiState.update { it.copy(mainPhotoUri = uri) }
    fun onGalleryChange(uris: List<Uri>) = _uiState.update { it.copy(galleryUris = uris) }
    fun onBirthDateChange(millis: Long) = _uiState.update { it.copy(birthDateMillis = millis) }
    fun onDescriptionChange(value: String) = _uiState.update { it.copy(description = value) }
    fun onSpeciesChange(species: Species) = _uiState.update { it.copy(species = species) }
    fun onCharacteristicToggle(characteristic: Characteristic) {
        _uiState.update { state ->
            val updated = if (characteristic in state.selectedCharacteristics) {
                state.selectedCharacteristics - characteristic
            } else {
                state.selectedCharacteristics + characteristic
            }
            state.copy(selectedCharacteristics = updated)
        }
    }

    fun onSave() {
        val shelterId = auth.currentUser?.uid
        if (shelterId == null) {
            _uiState.update {
                it.copy(saveMessageRes = R.string.pet_form_login_required, saveMessage = null)
            }
            return
        }

        val state = _uiState.value
        if (!state.canSave) return

        val id = state.animalId ?: UUID.randomUUID().toString()
        val mainPhoto = state.mainPhotoUri?.toString().orEmpty()
        val gallery = state.galleryUris.map { it.toString() }

        val animal = AnimalEntity(
            id,
            state.name.trim(),
            state.isFemale,
            mainPhoto,
            gallery,
            state.birthDateMillis,
            state.description.trim(),
            state.species,
            state.selectedCharacteristics.toList(),
            shelterId,

            //Aqui poner el valor de que se recoge en pantalla
            true,
        )

        animalRepository.updateAnimal(animal)
        _uiState.update {
            it.copy(
                saveSucceeded = true,
                saveMessageRes = R.string.animal_saved_successfully,
                saveMessage = null
            )
        }
    }

    fun onSaveHandled() {
        _uiState.update { it.copy(saveSucceeded = false, saveMessageRes = null, saveMessage = null) }
    }
}
