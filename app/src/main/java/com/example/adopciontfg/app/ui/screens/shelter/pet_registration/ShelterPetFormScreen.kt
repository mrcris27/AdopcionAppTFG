package com.example.adopciontfg.app.ui.screens.shelter.pet_registration

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.adopciontfg.R
import com.example.adopciontfg.model.Characteristic
import com.example.adopciontfg.model.Species
import com.example.adopciontfg.ui.theme.AdoptionTheme

@Composable
fun ShelterPetFormScreen(
    animalId: String?,
    onBackClick: () -> Unit,
    onSaved: () -> Unit,
    onDeleted: () -> Unit,
    viewModel: ShelterPetFormViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val message = uiState.saveMessageRes?.let { stringResource(it) }
        ?: uiState.saveMessage

    LaunchedEffect(animalId) {
        viewModel.loadAnimal(animalId)
    }

    LaunchedEffect(uiState.saveSucceeded) {
        if (uiState.saveSucceeded) {
            viewModel.onSaveHandled()
            onSaved()
        }
    }

    LaunchedEffect(uiState.deleteSucceeded) {
        if (uiState.deleteSucceeded) {
            viewModel.onDeleteHandled()
            onDeleted()
        }
    }

    LaunchedEffect(message) {
        val text = message ?: return@LaunchedEffect
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        viewModel.onMessageHandled()
    }

    PetRegistration(
        title = if (uiState.isEditMode) {
            stringResource(R.string.edit_animal)
        } else {
            stringResource(R.string.animal_registration)
        },
        saveButtonText = if (uiState.isEditMode) {
            stringResource(R.string.save_changes)
        } else {
            stringResource(R.string.register_animal)
        },
        isLoading = uiState.isLoading,
        isSaving = uiState.isSaving,
        savingMessage = if (uiState.isEditMode) {
            stringResource(R.string.saving_animal_info)
        } else {
            stringResource(R.string.registering_animal)
        },
        isDeleting = uiState.isDeleting,
        deletingMessage = stringResource(R.string.deleting_animal),
        name = uiState.name,
        onNameChange = viewModel::onNameChange,
        isFemale = uiState.isFemale,
        onSexChange = viewModel::onSexChange,
        mainPhotoUri = uiState.mainPhotoUri,
        onMainPhotoChange = viewModel::onMainPhotoChange,
        galleryUris = uiState.galleryUris,
        onGalleryChange = viewModel::onGalleryChange,
        birthDateMillis = uiState.birthDateMillis,
        onBirthDateChange = viewModel::onBirthDateChange,
        description = uiState.description,
        onDescriptionChange = viewModel::onDescriptionChange,
        isForAdoption = uiState.isForAdoption,
        onForAdoptionChange = viewModel::onForAdoptionChange,
        species = uiState.species,
        onSpeciesChange = viewModel::onSpeciesChange,
        selectedCharacteristics = uiState.selectedCharacteristics,
        onCharacteristicToggle = viewModel::onCharacteristicToggle,
        canSave = uiState.canSave,
        onBackClick = onBackClick,
        onSaveClick = viewModel::onSave,
        onDeleteClick = if (uiState.isEditMode) viewModel::onDelete else null,
    )
}

@Preview(showBackground = true, name = "Editar animal")
@Composable
private fun ShelterPetFormScreenPreview() {
    AdoptionTheme {
        PetRegistration(
            title = stringResource(R.string.edit_animal),
            saveButtonText = stringResource(R.string.save_changes),
            isLoading = false,
            isSaving = false,
            savingMessage = stringResource(R.string.saving_animal_info),
            deletingMessage = stringResource(R.string.deleting_animal),
            name = "Max",
            onNameChange = {},
            isFemale = false,
            onSexChange = {},
            mainPhotoUri = null,
            onMainPhotoChange = {},
            galleryUris = emptyList(),
            onGalleryChange = {},
            birthDateMillis = 1640995200000L,
            onBirthDateChange = {},
            description = "Perro cariñoso y activo que busca una familia responsable.",
            onDescriptionChange = {},
            isForAdoption = true,
            onForAdoptionChange = {},
            species = Species.PERRO,
            onSpeciesChange = {},
            selectedCharacteristics = setOf(
                Characteristic.JUGUETON,
                Characteristic.SOCIABLE_CON_KIDS,
            ),
            onCharacteristicToggle = {},
            canSave = true,
            onBackClick = {},
            onSaveClick = {},
            onDeleteClick = {},
        )
    }
}
