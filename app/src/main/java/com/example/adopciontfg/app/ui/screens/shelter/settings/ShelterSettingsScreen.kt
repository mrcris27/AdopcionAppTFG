package com.example.adopciontfg.app.ui.screens.shelter.settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.adopciontfg.app.ui.screens.components.AppFilledTextField
import com.example.adopciontfg.app.ui.screens.components.AppFormSection
import com.example.adopciontfg.app.ui.screens.components.AppOutlinedButton
import com.example.adopciontfg.app.ui.screens.components.AppPrimaryButton
import com.example.adopciontfg.app.ui.screens.components.AppTopAppBar
import com.example.adopciontfg.app.ui.screens.components.ProfilePhotoPicker
import com.example.adopciontfg.app.ui.screens.components.SavingOverlay
import com.example.adopciontfg.ui.theme.Dimens
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.adopciontfg.R
import com.example.adopciontfg.ui.theme.AdoptionTheme
import com.example.adopciontfg.ui.theme.inputOutlineUnfocused

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelterSettingsScreen(
    onBackClick: (() -> Unit)? = null,
    onLogout: () -> Unit,
    viewModel: ShelterSettingsViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val saveMessage = uiState.value.saveMessageRes?.let { stringResource(it) }
        ?: uiState.value.saveMessage

    LaunchedEffect(saveMessage) {
        val message = saveMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        viewModel.onMessageShown()
    }

    ShelterSettingsContent(
        uiState = uiState.value,
        onBackClick = onBackClick,
        onLogoutClick = {
            viewModel.logout()
            onLogout()
        },
        onShelterNameChange = viewModel::onShelterNameChange,
        onProfilePhotoChange = viewModel::onProfilePhotoChange,
        onEmailChange = viewModel::onEmailChange,
        onPhoneChange = viewModel::onPhoneChange,
        onStreetChange = viewModel::onStreetChange,
        onStreetNumberChange = viewModel::onStreetNumberChange,
        onPostalCodeChange = viewModel::onPostalCodeChange,
        onCityChange = viewModel::onCityChange,
        onProvinceChange = viewModel::onProvinceChange,
        onCifChange = viewModel::onCifChange,
        onAdoptionFormUrlChange = viewModel::onAdoptionFormUrlChange,
        onAdoptionAlertsChange = viewModel::onAdoptionAlertsChange,
        onDarkModeChange = viewModel::onDarkModeChange,
        onCurrentPasswordChange = viewModel::onCurrentPasswordChange,
        onNewPasswordChange = viewModel::onNewPasswordChange,
        onConfirmNewPasswordChange = viewModel::onConfirmNewPasswordChange,
        onToggleCurrentPasswordVisibility = viewModel::toggleCurrentPasswordVisibility,
        onToggleNewPasswordVisibility = viewModel::toggleNewPasswordVisibility,
        onToggleConfirmNewPasswordVisibility = viewModel::toggleConfirmNewPasswordVisibility,
        onOpenPasswordDialog = viewModel::onOpenPasswordDialog,
        onDismissPasswordDialog = viewModel::onDismissPasswordDialog,
        onSaveClick = viewModel::onSaveClick,
        onChangePasswordClick = viewModel::onChangePasswordClick,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShelterSettingsContent(
    uiState: ShelterSettingsUiState,
    onBackClick: (() -> Unit)?,
    onLogoutClick: () -> Unit,
    onShelterNameChange: (String) -> Unit,
    onProfilePhotoChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onStreetChange: (String) -> Unit,
    onStreetNumberChange: (String) -> Unit,
    onPostalCodeChange: (String) -> Unit,
    onCityChange: (String) -> Unit,
    onProvinceChange: (String) -> Unit,
    onCifChange: (String) -> Unit,
    onAdoptionFormUrlChange: (String) -> Unit,
    onAdoptionAlertsChange: (Boolean) -> Unit,
    onDarkModeChange: (Boolean) -> Unit,
    onCurrentPasswordChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onConfirmNewPasswordChange: (String) -> Unit,
    onToggleCurrentPasswordVisibility: () -> Unit,
    onToggleNewPasswordVisibility: () -> Unit,
    onToggleConfirmNewPasswordVisibility: () -> Unit,
    onOpenPasswordDialog: () -> Unit,
    onDismissPasswordDialog: () -> Unit,
    onSaveClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    if (uiState.isPasswordChangeDialogOpen) {
        ChangePasswordDialog(
            uiState = uiState,
            onDismissRequest = onDismissPasswordDialog,
            onCurrentPasswordChange = onCurrentPasswordChange,
            onNewPasswordChange = onNewPasswordChange,
            onConfirmNewPasswordChange = onConfirmNewPasswordChange,
            onToggleCurrentPasswordVisibility = onToggleCurrentPasswordVisibility,
            onToggleNewPasswordVisibility = onToggleNewPasswordVisibility,
            onToggleConfirmNewPasswordVisibility = onToggleConfirmNewPasswordVisibility,
            onConfirmClick = onChangePasswordClick
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                AppTopAppBar(
                    title = stringResource(R.string.ajustes_protectora),
                    onBackClick = onBackClick
                )
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(Dimens.screenPadding),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
            ) {
                AppFormSection(title = stringResource(R.string.datos_protectora)) {
                    ProfilePhotoPicker(
                        photoUri = uiState.profilePhotoUri,
                        onPhotoChange = onProfilePhotoChange,
                        modifier = Modifier.fillMaxWidth()
                    )
                    AppFilledTextField(
                        value = uiState.shelterName,
                        onValueChange = onShelterNameChange,
                        label = { Text(stringResource(R.string.nombre_protectora)) }
                    )
                    AppFilledTextField(
                        value = uiState.email,
                        onValueChange = onEmailChange,
                        label = { Text(stringResource(R.string.correo)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                    AppFilledTextField(
                        value = uiState.phone,
                        onValueChange = onPhoneChange,
                        label = { Text(stringResource(R.string.telefono)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                    AppFilledTextField(
                        value = uiState.cif,
                        onValueChange = onCifChange,
                        label = { Text(stringResource(R.string.cif)) }
                    )
                }

                AppFormSection(title = stringResource(R.string.direccion)) {
                    AppFilledTextField(
                        value = uiState.street,
                        onValueChange = onStreetChange,
                        label = { Text(stringResource(R.string.calle)) },
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                    )
                    AppFilledTextField(
                        value = uiState.streetNumber,
                        onValueChange = onStreetNumberChange,
                        label = { Text(stringResource(R.string.numero)) },
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters)
                    )
                    AppFilledTextField(
                        value = uiState.postalCode,
                        onValueChange = onPostalCodeChange,
                        label = { Text(stringResource(R.string.codigo_postal)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    AppFilledTextField(
                        value = uiState.city,
                        onValueChange = onCityChange,
                        label = { Text(stringResource(R.string.localidad_ciudad)) },
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                    )
                    ShelterSettingsProvinceDropdown(
                        selectedProvince = uiState.province,
                        provinces = stringArrayResource(R.array.provincias_espana).toList(),
                        onProvinceSelected = onProvinceChange
                    )
                }

                AppFormSection(title = stringResource(R.string.adopciones)) {
                    Text(
                        text = stringResource(R.string.adopciones_formulario_desc),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    AppFilledTextField(
                        value = uiState.adoptionFormUrl,
                        onValueChange = onAdoptionFormUrlChange,
                        label = { Text(stringResource(R.string.enlace_google_forms)) },
                        placeholder = { Text(stringResource(R.string.google_forms_placeholder)) }
                    )
                }

                AppFormSection(title = stringResource(R.string.preferencias)) {
                    SwitchRow(
                        text = stringResource(R.string.alertas_adopcion),
                        icon = { Icon(Icons.Outlined.Campaign, contentDescription = null) },
                        checked = uiState.adoptionAlertsEnabled,
                        onCheckedChange = onAdoptionAlertsChange
                    )
                    SwitchRow(
                        text = stringResource(R.string.modo_oscuro),
                        icon = { Icon(Icons.Outlined.Palette, contentDescription = null) },
                        checked = uiState.darkModeEnabled,
                        onCheckedChange = onDarkModeChange
                    )
                }

                AppFormSection(title = stringResource(R.string.seguridad)) {
                    Text(
                        text = stringResource(R.string.actualizar_contrasena_desde_formulario),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    AppPrimaryButton(
                        text = stringResource(R.string.cambiar_contrasena),
                        onClick = onOpenPasswordDialog
                    )
                }
                AppPrimaryButton(
                    text = stringResource(R.string.guardar),
                    onClick = onSaveClick,
                    enabled = uiState.canSaveSettings
                )
                AppOutlinedButton(
                    text = stringResource(R.string.cerrar_sesion),
                    onClick = onLogoutClick
                )
            }
        }

        if (uiState.isSavingSettings) {
            SavingOverlay(message = stringResource(R.string.saving_shelter_settings))
        }
        if (uiState.isPasswordChangeLoading) {
            SavingOverlay(message = stringResource(R.string.updating_password_overlay))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShelterSettingsProvinceDropdown(
    selectedProvince: String,
    provinces: List<String>,
    onProvinceSelected: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            value = selectedProvince,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.provincia)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.inputOutlineUnfocused(),
            ),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            provinces.forEach { province ->
                DropdownMenuItem(
                    text = { Text(province) },
                    onClick = {
                        onProvinceSelected(province)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ShelterSettingsScreenPreview() {
    AdoptionTheme {
        ShelterSettingsContent(
            uiState = ShelterSettingsUiState(),
            onBackClick = {},
            onLogoutClick = {},
            onShelterNameChange = {},
            onProfilePhotoChange = {},
            onEmailChange = {},
            onPhoneChange = {},
            onStreetChange = {},
            onStreetNumberChange = {},
            onPostalCodeChange = {},
            onCityChange = {},
            onProvinceChange = {},
            onCifChange = {},
            onAdoptionFormUrlChange = {},
            onAdoptionAlertsChange = {},
            onDarkModeChange = {},
            onCurrentPasswordChange = {},
            onNewPasswordChange = {},
            onConfirmNewPasswordChange = {},
            onToggleCurrentPasswordVisibility = {},
            onToggleNewPasswordVisibility = {},
            onToggleConfirmNewPasswordVisibility = {},
            onOpenPasswordDialog = {},
            onDismissPasswordDialog = {},
            onSaveClick = {},
            onChangePasswordClick = {}
        )
    }
}

@Composable
private fun SwitchRow(
    text: String,
    icon: @Composable () -> Unit,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()
            Text(text)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun ChangePasswordDialog(
    uiState: ShelterSettingsUiState,
    onDismissRequest: () -> Unit,
    onCurrentPasswordChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onConfirmNewPasswordChange: (String) -> Unit,
    onToggleCurrentPasswordVisibility: () -> Unit,
    onToggleNewPasswordVisibility: () -> Unit,
    onToggleConfirmNewPasswordVisibility: () -> Unit,
    onConfirmClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.cambiar_contrasena)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)) {
                PasswordField(
                    value = uiState.currentPassword,
                    onValueChange = onCurrentPasswordChange,
                    label = stringResource(R.string.contrasena_actual),
                    hidden = uiState.currentPasswordHidden,
                    onToggleVisibility = onToggleCurrentPasswordVisibility
                )
                PasswordField(
                    value = uiState.newPassword,
                    onValueChange = onNewPasswordChange,
                    label = stringResource(R.string.nueva_contrasena),
                    hidden = uiState.newPasswordHidden,
                    onToggleVisibility = onToggleNewPasswordVisibility
                )
                PasswordField(
                    value = uiState.confirmNewPassword,
                    onValueChange = onConfirmNewPasswordChange,
                    label = stringResource(R.string.repetir_nueva_contrasena),
                    hidden = uiState.confirmNewPasswordHidden,
                    onToggleVisibility = onToggleConfirmNewPasswordVisibility
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirmClick,
                enabled = !uiState.isPasswordChangeLoading
            ) {
                Text(
                    if (uiState.isPasswordChangeLoading) {
                        stringResource(R.string.actualizando)
                    } else {
                        stringResource(R.string.actualizar)
                    }
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                enabled = !uiState.isPasswordChangeLoading
            ) {
                Text(stringResource(R.string.cancelar))
            }
        }
    )
}

@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    hidden: Boolean,
    onToggleVisibility: () -> Unit
) {
    AppFilledTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        visualTransformation = if (hidden) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                Icon(
                    imageVector = if (hidden) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = null
                )
            }
        }
    )
}

