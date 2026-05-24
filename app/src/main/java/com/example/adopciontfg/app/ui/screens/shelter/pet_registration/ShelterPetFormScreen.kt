package com.example.adopciontfg.app.ui.screens.shelter.pet_registration

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
    viewModel: ShelterPetFormViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(animalId) {
        viewModel.loadAnimal(animalId)
    }

    LaunchedEffect(uiState.saveSucceeded) {
        if (uiState.saveSucceeded) {
            viewModel.onSaveHandled()
            onSaved()
        }
    }

    PetRegistration(
        title = if (uiState.isEditMode) {
            stringResource(R.string.editar_animal)
        } else {
            stringResource(R.string.registro_animal)
        },
        saveButtonText = if (uiState.isEditMode) {
            stringResource(R.string.guardar_cambios)
        } else {
            stringResource(R.string.registrar_animal)
        },
        isLoading = uiState.isLoading,
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
        species = uiState.species,
        onSpeciesChange = viewModel::onSpeciesChange,
        selectedCharacteristics = uiState.selectedCharacteristics,
        onCharacteristicToggle = viewModel::onCharacteristicToggle,
        canSave = uiState.canSave,
        onBackClick = onBackClick,
        onSaveClick = viewModel::onSave,
    )
}

@Preview(showBackground = true, name = "Editar animal")
@Composable
private fun ShelterPetFormScreenPreview() {
    AdoptionTheme {
        PetRegistration(
            title = stringResource(R.string.editar_animal),
            saveButtonText = stringResource(R.string.guardar_cambios),
            isLoading = false,
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
        )
    }
}
