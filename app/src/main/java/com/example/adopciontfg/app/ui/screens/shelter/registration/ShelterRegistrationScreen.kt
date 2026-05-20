package com.example.adopciontfg.app.ui.screens.shelter.registration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.adopciontfg.R
import com.example.adopciontfg.app.ui.screens.components.AppSecondaryButton
import com.example.adopciontfg.app.ui.screens.components.AppTopAppBar
import com.example.adopciontfg.app.ui.screens.components.ProfilePhotoPicker
import com.example.adopciontfg.app.ui.screens.components.RegistrationPasswordField
import com.example.adopciontfg.app.ui.screens.components.RegistrationTextField
import com.example.adopciontfg.ui.theme.AdoptionTheme
import com.example.adopciontfg.ui.theme.Dimens

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
    viewModel: ShelterRegistrationViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

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
                value = uiState.value.address,
                onValueChange = viewModel::onAddressChange,
                label = stringResource(R.string.direccion)
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

            ProfilePhotoPicker(
                photoUri = uiState.value.profilePhotoUri,
                onPhotoChange = viewModel::onProfilePhotoChange,
                modifier = Modifier.fillMaxWidth()
            )

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
                enabled = uiState.value.canRegister,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

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
