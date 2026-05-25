package com.example.adopciontfg.app.ui.screens.shelter.registration

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.adopciontfg.R
import com.example.adopciontfg.app.ui.screens.components.AppFormSection
import com.example.adopciontfg.app.ui.screens.components.AppSecondaryButton
import com.example.adopciontfg.app.ui.screens.components.AppTopAppBar
import com.example.adopciontfg.app.ui.screens.components.ProfilePhotoPicker
import com.example.adopciontfg.app.ui.screens.components.RegistrationPasswordField
import com.example.adopciontfg.app.ui.screens.components.RegistrationTextField
import com.example.adopciontfg.app.ui.screens.components.SavingOverlay
import com.example.adopciontfg.ui.theme.AdoptionTheme
import com.example.adopciontfg.ui.theme.Dimens
import com.example.adopciontfg.ui.theme.inputOutlineUnfocused

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelterRegistration(
    onRegisterClick: (
        name: String,
        cif: String,
        phone: String,
        address: String,
        profilePhotoUri: String,
        email: String,
        password: String
    ) -> Unit,
    onBackClick: () -> Unit,
    isSaving: Boolean = false,
    viewModel: ShelterRegistrationViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                AppTopAppBar(
                    title = stringResource(R.string.registro_protectora),
                    onBackClick = onBackClick
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(Dimens.screenPadding)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AppFormSection(title = stringResource(R.string.datos_protectora)) {
                    RegistrationTextField(
                        value = uiState.value.name,
                        onValueChange = viewModel::onNameChange,
                        label = stringResource(R.string.nombre)
                    )
                    RegistrationTextField(
                        value = uiState.value.cif,
                        onValueChange = viewModel::onCifChange,
                        label = stringResource(R.string.cif)
                    )
                    RegistrationTextField(
                        value = uiState.value.phone,
                        onValueChange = viewModel::onPhoneChange,
                        label = stringResource(R.string.telefono),
                        keyboardType = KeyboardType.Phone
                    )
                    RegistrationTextField(
                        value = uiState.value.email,
                        onValueChange = viewModel::onEmailChange,
                        label = stringResource(R.string.correo),
                        keyboardType = KeyboardType.Email,
                        errorMessage = if (uiState.value.isEmailInvalid) {
                            stringResource(R.string.login_invalid_email_error)
                        } else {
                            null
                        }
                    )
                }

                AppFormSection(title = stringResource(R.string.direccion)) {
                    RegistrationTextField(
                        value = uiState.value.street,
                        onValueChange = viewModel::onStreetChange,
                        label = stringResource(R.string.calle),
                        capitalization = KeyboardCapitalization.Words
                    )
                    RegistrationTextField(
                        value = uiState.value.streetNumber,
                        onValueChange = viewModel::onStreetNumberChange,
                        label = stringResource(R.string.numero),
                        capitalization = KeyboardCapitalization.Characters
                    )
                    RegistrationTextField(
                        value = uiState.value.postalCode,
                        onValueChange = viewModel::onPostalCodeChange,
                        label = stringResource(R.string.codigo_postal),
                        keyboardType = KeyboardType.Number
                    )
                    RegistrationTextField(
                        value = uiState.value.city,
                        onValueChange = viewModel::onCityChange,
                        label = stringResource(R.string.localidad_ciudad),
                        capitalization = KeyboardCapitalization.Words
                    )
                    ShelterProvinceDropdown(
                        selectedProvince = uiState.value.province,
                        provinces = stringArrayResource(R.array.provincias_espana).toList(),
                        onProvinceSelected = viewModel::onProvinceChange
                    )
                }

                AppFormSection(title = stringResource(R.string.acceso)) {
                    RegistrationPasswordField(
                        value = uiState.value.password,
                        onValueChange = viewModel::onPasswordChange,
                        label = stringResource(R.string.contraseña),
                        hidden = uiState.value.passwordHidden,
                        onToggleVisibility = viewModel::togglePasswordVisibility,
                        errorMessage = if (uiState.value.isPasswordInvalid) {
                            stringResource(R.string.registro_password_min_length_error)
                        } else {
                            null
                        }
                    )
                    RegistrationPasswordField(
                        value = uiState.value.confirmPassword,
                        onValueChange = viewModel::onConfirmPasswordChange,
                        label = stringResource(R.string.contraseña2),
                        hidden = uiState.value.confirmPasswordHidden,
                        onToggleVisibility = viewModel::toggleConfirmPasswordVisibility,
                        errorMessage = if (uiState.value.doPasswordsNotMatch) {
                            stringResource(R.string.registro_passwords_do_not_match_error)
                        } else {
                            null
                        }
                    )
                }

                AppFormSection(title = stringResource(R.string.imagen_publica)) {
                    ProfilePhotoPicker(
                        photoUri = uiState.value.profilePhotoUri,
                        onPhotoChange = viewModel::onProfilePhotoChange,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                AppSecondaryButton(
                    text = stringResource(R.string.registrar),
                    onClick = {
                        with(uiState.value) {
                            onRegisterClick(
                                name.trim(),
                                cif.trim(),
                                phone.trim(),
                                address.trim(),
                                profilePhotoUri,
                                email.trim(),
                                password
                            )
                        }
                    },
                    enabled = uiState.value.canRegister && !isSaving,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (isSaving) {
            SavingOverlay(message = stringResource(R.string.saving_shelter_registration))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShelterProvinceDropdown(
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
            keyboardOptions = KeyboardOptions.Default,
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

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun ShelterRegistrationPreview() {
    AdoptionTheme {
        ShelterRegistration(
            onRegisterClick = { _, _, _, _, _, _, _ -> },
            onBackClick = {},
            viewModel = ShelterRegistrationViewModel()
        )
    }
}
