package com.example.adopciontfg.app.ui.screens.user.registration

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import com.example.adopciontfg.app.ui.screens.components.AppFormSection
import com.example.adopciontfg.app.ui.screens.components.AppSecondaryButton
import com.example.adopciontfg.app.ui.screens.components.AppTopAppBar
import com.example.adopciontfg.app.ui.screens.components.ProfilePhotoPicker
import com.example.adopciontfg.app.ui.screens.components.RegistrationPasswordField
import com.example.adopciontfg.app.ui.screens.components.RegistrationTextField
import com.example.adopciontfg.app.ui.screens.components.SavingOverlay
import com.example.adopciontfg.ui.theme.AdoptionTheme
import com.example.adopciontfg.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserRegistration(
    onBackClick: () -> Unit,
    onRegisterClick: (
        name: String,
        surname: String,
        email: String,
        biography: String,
        profilePhotoUri: String,
        password: String
    ) -> Unit,
    isSaving: Boolean = false,
    viewModel: UserRegistrationViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                AppTopAppBar(
                    title = stringResource(R.string.user_registration),
                    onBackClick = onBackClick
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = Dimens.spacingSm, vertical = Dimens.screenPadding)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AppFormSection(title = stringResource(R.string.personal_details)) {
                    RegistrationTextField(
                        value = uiState.value.name,
                        onValueChange = viewModel::onNameChange,
                        label = stringResource(R.string.name),
                        required = true
                    )
                    RegistrationTextField(
                        value = uiState.value.surname,
                        onValueChange = viewModel::onSurnameChange,
                        label = stringResource(R.string.surnames),
                        required = true
                    )
                    RegistrationTextField(
                        value = uiState.value.email,
                        onValueChange = viewModel::onEmailChange,
                        label = stringResource(R.string.email),
                        keyboardType = KeyboardType.Email,
                        required = true,
                        errorMessage = if (uiState.value.isEmailInvalid) {
                            stringResource(R.string.login_invalid_email_error)
                        } else {
                            null
                        }
                    )
                    RegistrationTextField(
                        value = uiState.value.biography,
                        onValueChange = viewModel::onBiographyChange,
                        label = stringResource(R.string.biography),
                        singleLine = false,
                        minLines = 3,
                        required = true
                    )
                }

                AppFormSection(title = stringResource(R.string.profile_photo_section)) {
                    ProfilePhotoPicker(
                        photoUri = uiState.value.profilePhotoUri,
                        onPhotoChange = viewModel::onProfilePhotoChange,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                AppFormSection(title = stringResource(R.string.access)) {
                    RegistrationPasswordField(
                        value = uiState.value.password,
                        onValueChange = viewModel::onPasswordChange,
                        label = stringResource(R.string.password),
                        hidden = uiState.value.passwordHidden,
                        onToggleVisibility = viewModel::togglePasswordVisibility,
                        required = true,
                        errorMessage = if (uiState.value.isPasswordInvalid) {
                            stringResource(R.string.registration_password_min_length_error)
                        } else {
                            null
                        }
                    )
                    RegistrationPasswordField(
                        value = uiState.value.confirmPassword,
                        onValueChange = viewModel::onConfirmPasswordChange,
                        label = stringResource(R.string.repeat_password),
                        hidden = uiState.value.confirmPasswordHidden,
                        onToggleVisibility = viewModel::toggleConfirmPasswordVisibility,
                        required = true,
                        errorMessage = if (uiState.value.doPasswordsNotMatch) {
                            stringResource(R.string.registration_passwords_do_not_match_error)
                        } else {
                            null
                        }
                    )
                }

                AppSecondaryButton(
                    text = stringResource(R.string.register),
                    onClick = {
                        with(uiState.value) {
                            onRegisterClick(
                                name.trim(),
                                surname.trim(),
                                email.trim(),
                                biography.trim(),
                                profilePhotoUri,
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
            SavingOverlay(message = stringResource(R.string.saving_user_registration))
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun UserRegistrationPreview() {
    AdoptionTheme {
        UserRegistration(
            onBackClick = {},
            onRegisterClick = { _, _, _, _, _, _ -> },
            viewModel = UserRegistrationViewModel()
        )
    }
}
