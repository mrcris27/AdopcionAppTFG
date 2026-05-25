package com.example.adopciontfg.app.ui.screens.shelter.pet_registration

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import com.example.adopciontfg.R
import com.example.adopciontfg.app.ui.screens.components.AppFormSection
import com.example.adopciontfg.app.ui.screens.components.AppOutlinedButton
import com.example.adopciontfg.app.ui.screens.components.AppSecondaryButton
import com.example.adopciontfg.app.ui.screens.components.AppTopAppBar
import com.example.adopciontfg.app.ui.screens.components.RegistrationTextField
import com.example.adopciontfg.app.ui.screens.components.SavingOverlay
import com.example.adopciontfg.app.ui.screens.components.SavePhotos
import com.example.adopciontfg.app.ui.screens.components.characteristicLabel
import com.example.adopciontfg.app.ui.screens.components.speciesLabel
import com.example.adopciontfg.model.Characteristic
import com.example.adopciontfg.model.Species
import com.example.adopciontfg.ui.theme.AdoptionTheme
import com.example.adopciontfg.ui.theme.Dimens
import com.example.adopciontfg.ui.theme.inputOutlineUnfocused
import com.example.adopciontfg.ui.theme.subtleDivider
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val MAX_MAIN_PHOTOS = 1
private const val MAX_GALLERY_PHOTOS = 6

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PetRegistration(
    onBackClick: (() -> Unit)? = null,
    onRegisterClick: () -> Unit = {},
) {
    var name by rememberSaveable { mutableStateOf("") }
    var isFemale by rememberSaveable { mutableStateOf(false) }
    var mainPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var galleryUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var birthDateMillis by rememberSaveable { mutableLongStateOf(0L) }
    var description by rememberSaveable { mutableStateOf("") }
    var isForAdoption by rememberSaveable { mutableStateOf(true) }
    var species by remember { mutableStateOf<Species?>(null) }
    var selectedCharacteristics by remember { mutableStateOf(setOf<Characteristic>()) }

    val canRegister = name.isNotBlank() &&
        species != null &&
        birthDateMillis > 0L &&
        mainPhotoUri != null

    PetRegistration(
        title = stringResource(R.string.animal_registration),
        saveButtonText = stringResource(R.string.register_animal),
        isLoading = false,
        isSaving = false,
        savingMessage = stringResource(R.string.registering_animal),
        deletingMessage = stringResource(R.string.deleting_animal),
        name = name,
        onNameChange = { name = it },
        isFemale = isFemale,
        onSexChange = { isFemale = it },
        mainPhotoUri = mainPhotoUri,
        onMainPhotoChange = { mainPhotoUri = it },
        galleryUris = galleryUris,
        onGalleryChange = { galleryUris = it },
        birthDateMillis = birthDateMillis,
        onBirthDateChange = { birthDateMillis = it },
        description = description,
        onDescriptionChange = { description = it },
        isForAdoption = isForAdoption,
        onForAdoptionChange = { isForAdoption = it },
        species = species,
        onSpeciesChange = { species = it },
        selectedCharacteristics = selectedCharacteristics,
        onCharacteristicToggle = { characteristic ->
            selectedCharacteristics = if (characteristic in selectedCharacteristics) {
                selectedCharacteristics - characteristic
            } else {
                selectedCharacteristics + characteristic
            }
        },
        canSave = canRegister,
        onBackClick = onBackClick,
        onSaveClick = onRegisterClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PetRegistration(
    title: String,
    saveButtonText: String,
    isLoading: Boolean,
    isSaving: Boolean,
    savingMessage: String,
    isDeleting: Boolean = false,
    deletingMessage: String,
    name: String,
    onNameChange: (String) -> Unit,
    isFemale: Boolean,
    onSexChange: (Boolean) -> Unit,
    mainPhotoUri: Uri?,
    onMainPhotoChange: (Uri?) -> Unit,
    galleryUris: List<Uri>,
    onGalleryChange: (List<Uri>) -> Unit,
    birthDateMillis: Long,
    onBirthDateChange: (Long) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    isForAdoption: Boolean,
    onForAdoptionChange: (Boolean) -> Unit,
    species: Species?,
    onSpeciesChange: (Species) -> Unit,
    selectedCharacteristics: Set<Characteristic>,
    onCharacteristicToggle: (Characteristic) -> Unit,
    canSave: Boolean,
    onBackClick: (() -> Unit)? = null,
    onSaveClick: () -> Unit,
    onDeleteClick: (() -> Unit)? = null,
) {
    var speciesExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                AppTopAppBar(
                    title = title,
                    onBackClick = onBackClick,
                    actions = {
                        if (onDeleteClick != null) {
                            IconButton(
                                onClick = { showDeleteDialog = true },
                                enabled = !isLoading && !isSaving && !isDeleting,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = stringResource(R.string.delete_animal),
                                    tint = MaterialTheme.colorScheme.error,
                                )
                            }
                        }
                    },
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Dimens.screenPadding, vertical = Dimens.spacingMd),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
            ) {
                AppFormSection(title = stringResource(R.string.basic_details)) {
                    RegistrationTextField(
                        value = name,
                        onValueChange = onNameChange,
                        label = stringResource(R.string.name),
                        capitalization = KeyboardCapitalization.Words
                    )

                    ExposedDropdownMenuBox(
                        expanded = speciesExpanded,
                        onExpandedChange = { speciesExpanded = !speciesExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextField(
                            value = if (species != null) {
                                speciesLabel(species)
                            } else {
                                ""
                            },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.species)) },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = speciesExpanded)
                            },
                            modifier = Modifier
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.inputOutlineUnfocused(),
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = speciesExpanded,
                            onDismissRequest = { speciesExpanded = false }
                        ) {
                            Species.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(speciesLabel(option)) },
                                    onClick = {
                                        onSpeciesChange(option)
                                        speciesExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Text(
                        text = stringResource(R.string.sex),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
                    ) {
                        FilterChip(
                            selected = !isFemale,
                            onClick = { onSexChange(false) },
                            label = { Text(stringResource(R.string.male)) },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = isFemale,
                            onClick = { onSexChange(true) },
                            label = { Text(stringResource(R.string.female)) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    AdoptionAvailabilitySwitchRow(
                        isForAdoption = isForAdoption,
                        onForAdoptionChange = onForAdoptionChange
                    )

                    Text(
                        text = stringResource(R.string.birth_date),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    AppOutlinedButton(
                        text = if (birthDateMillis > 0L) {
                            dateFormatter.format(Date(birthDateMillis))
                        } else {
                            stringResource(R.string.select_date)
                        },
                        onClick = { showDatePicker = true }
                    )
                }

                AppFormSection(title = stringResource(R.string.description)) {
                    RegistrationTextField(
                        value = description,
                        onValueChange = onDescriptionChange,
                        label = stringResource(R.string.description),
                        singleLine = false,
                        minLines = 4
                    )
                }

                AppFormSection(title = stringResource(R.string.photos)) {
                    Text(
                        text = stringResource(R.string.main_photo),
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = stringResource(R.string.main_photo_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    SavePhotos(
                        uris = listOfNotNull(mainPhotoUri),
                        onUrisChange = { uris -> onMainPhotoChange(uris.firstOrNull()) },
                        maxPhotos = MAX_MAIN_PHOTOS
                    )

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.subtleDivider()
                    )

                    Text(
                        text = stringResource(R.string.more_photos),
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = stringResource(R.string.more_photos_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    SavePhotos(
                        uris = galleryUris,
                        onUrisChange = onGalleryChange,
                        maxPhotos = MAX_GALLERY_PHOTOS
                    )
                }

                AppFormSection(title = stringResource(R.string.characteristics)) {
                    Text(
                        text = stringResource(R.string.characteristics_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
                        verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Characteristic.entries.forEach { characteristic ->
                            FilterChip(
                                selected = characteristic in selectedCharacteristics,
                                onClick = { onCharacteristicToggle(characteristic) },
                                label = { Text(characteristicLabel(characteristic)) }
                            )
                        }
                    }
                }

                AppSecondaryButton(
                    text = saveButtonText,
                    onClick = onSaveClick,
                    enabled = canSave && !isLoading && !isSaving && !isDeleting
                )

                Spacer(modifier = Modifier.height(Dimens.spacingSm))
            }
        }

        if (isSaving) {
            SavingOverlay(message = savingMessage)
        }
        if (isDeleting) {
            SavingOverlay(message = deletingMessage)
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = birthDateMillis.takeIf { it > 0L }
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        onBirthDateChange(datePickerState.selectedDateMillis ?: 0L)
                        showDatePicker = false
                    }
                ) {
                    Text(stringResource(android.R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(android.R.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_animal)) },
            text = { Text(stringResource(R.string.confirm_delete_animal)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteClick?.invoke()
                    },
                ) {
                    Text(
                        text = stringResource(R.string.delete),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

@Composable
private fun AdoptionAvailabilitySwitchRow(
    isForAdoption: Boolean,
    onForAdoptionChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.adopt),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge
        )
        Switch(
            checked = isForAdoption,
            onCheckedChange = onForAdoptionChange
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PetRegistrationPreview() {
    AdoptionTheme {
        PetRegistration()
    }
}
