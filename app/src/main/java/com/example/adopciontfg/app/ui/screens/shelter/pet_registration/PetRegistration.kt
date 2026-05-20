package com.example.adopciontfg.app.ui.screens.shelter.pet_registration

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.example.adopciontfg.app.ui.screens.components.SavePhotos
import com.example.adopciontfg.model.AnimalStatus
import com.example.adopciontfg.model.Characteristic
import com.example.adopciontfg.model.Species
import com.example.adopciontfg.ui.theme.AdoptionTheme
import com.example.adopciontfg.ui.theme.Dimens
import com.example.adopciontfg.ui.theme.inputOutlineUnfocused
import com.example.adopciontfg.ui.theme.subtleDivider
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    var species by remember { mutableStateOf<Species?>(null) }
    var selectedCharacteristics by remember { mutableStateOf(setOf<Characteristic>()) }

    val canRegister = name.isNotBlank() &&
        species != null &&
        birthDateMillis > 0L &&
        mainPhotoUri != null

    PetRegistration(
        title = stringResource(R.string.registro_animal),
        saveButtonText = stringResource(R.string.registrar_animal),
        isLoading = false,
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
        status = AnimalStatus.AVAILABLE,
        onStatusChange = {},
        showStatusSection = false,
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
    species: Species?,
    onSpeciesChange: (Species) -> Unit,
    selectedCharacteristics: Set<Characteristic>,
    onCharacteristicToggle: (Characteristic) -> Unit,
    status: AnimalStatus,
    onStatusChange: (AnimalStatus) -> Unit,
    showStatusSection: Boolean,
    canSave: Boolean,
    onBackClick: (() -> Unit)? = null,
    onSaveClick: () -> Unit,
) {
    var speciesExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopAppBar(
                title = title,
                onBackClick = onBackClick,
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
            AppFormSection(title = stringResource(R.string.datos_basicos)) {
                RegistrationTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = stringResource(R.string.nombre),
                    capitalization = KeyboardCapitalization.Words
                )

                ExposedDropdownMenuBox(
                    expanded = speciesExpanded,
                    onExpandedChange = { speciesExpanded = !speciesExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = species?.let { enumLabel(it.name) }.orEmpty(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.especie)) },
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
                                text = { Text(enumLabel(option.name)) },
                                onClick = {
                                    onSpeciesChange(option)
                                    speciesExpanded = false
                                }
                            )
                        }
                    }
                }

                Text(
                    text = stringResource(R.string.sexo),
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
                        label = { Text(stringResource(R.string.macho)) },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = isFemale,
                        onClick = { onSexChange(true) },
                        label = { Text(stringResource(R.string.hembra)) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(
                    text = stringResource(R.string.fecha_nacimiento),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                AppOutlinedButton(
                    text = if (birthDateMillis > 0L) {
                        dateFormatter.format(Date(birthDateMillis))
                    } else {
                        stringResource(R.string.seleccionar_fecha)
                    },
                    onClick = { showDatePicker = true }
                )
            }

            AppFormSection(title = stringResource(R.string.descripcion)) {
                RegistrationTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    label = stringResource(R.string.descripcion),
                    singleLine = false,
                    minLines = 4
                )
            }

            AppFormSection(title = stringResource(R.string.fotos)) {
                Text(
                    text = stringResource(R.string.foto_principal),
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = stringResource(R.string.foto_principal_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                SavePhotos(
                    uris = listOfNotNull(mainPhotoUri),
                    onUrisChange = { uris -> onMainPhotoChange(uris.firstOrNull()) },
                    maxPhotos = 1
                )

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.subtleDivider()
                )

                Text(
                    text = stringResource(R.string.mas_fotos),
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = stringResource(R.string.mas_fotos_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                SavePhotos(
                    uris = galleryUris,
                    onUrisChange = onGalleryChange
                )
            }

            AppFormSection(title = stringResource(R.string.caracteristicas)) {
                Text(
                    text = stringResource(R.string.caracteristicas_hint),
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
                            label = { Text(enumLabel(characteristic.name)) }
                        )
                    }
                }
            }

            if (showStatusSection) {
                AppFormSection(title = stringResource(R.string.estado_publicacion)) {
                    Text(
                        text = stringResource(R.string.estado_publicacion_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    ExposedDropdownMenuBox(
                        expanded = statusExpanded,
                        onExpandedChange = { statusExpanded = !statusExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextField(
                            value = enumLabel(status.name),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.estado_publicacion)) },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded)
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
                            expanded = statusExpanded,
                            onDismissRequest = { statusExpanded = false }
                        ) {
                            AnimalStatus.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(enumLabel(option.name)) },
                                    onClick = {
                                        onStatusChange(option)
                                        statusExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            AppSecondaryButton(
                text = saveButtonText,
                onClick = onSaveClick,
                enabled = canSave && !isLoading
            )

            Spacer(modifier = Modifier.height(Dimens.spacingSm))
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
}

private fun enumLabel(name: String): String =
    name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }

@Preview(showBackground = true)
@Composable
fun PetRegistrationPreview() {
    AdoptionTheme {
        PetRegistration()
    }
}
