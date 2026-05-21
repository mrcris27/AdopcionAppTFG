package com.example.adopciontfg.app.ui.screens.shelter.settings
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.adopciontfg.app.ui.screens.components.AppFilledTextField
import com.example.adopciontfg.app.ui.screens.components.AppFormSection
import com.example.adopciontfg.app.ui.screens.components.AppOutlinedButton
import com.example.adopciontfg.app.ui.screens.components.AppPrimaryButton
import com.example.adopciontfg.app.ui.screens.components.AppTopAppBar
import com.example.adopciontfg.app.ui.screens.components.ProfilePhotoPicker
import com.example.adopciontfg.ui.theme.Dimens
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.adopciontfg.ui.theme.AdoptionTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelterSettingsScreen(
    onBackClick: (() -> Unit)? = null,
    onLogout: () -> Unit,
    viewModel: ShelterSettingsViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.value.saveMessage) {
        val message = uiState.value.saveMessage ?: return@LaunchedEffect
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
        onAddressChange = viewModel::onAddressChange,
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
    onAddressChange: (String) -> Unit,
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

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopAppBar(
                title = "Ajustes de protectora",
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
            AppFormSection(title = "Datos de la protectora") {
                ProfilePhotoPicker(
                    photoUri = uiState.profilePhotoUri,
                    onPhotoChange = onProfilePhotoChange,
                    modifier = Modifier.fillMaxWidth()
                )
                AppFilledTextField(
                    value = uiState.shelterName,
                    onValueChange = onShelterNameChange,
                    label = { Text("Nombre de protectora") }
                )
                AppFilledTextField(
                    value = uiState.email,
                    onValueChange = onEmailChange,
                    label = { Text("Correo") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                AppFilledTextField(
                    value = uiState.phone,
                    onValueChange = onPhoneChange,
                    label = { Text("Telefono") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
                AppFilledTextField(
                    value = uiState.address,
                    onValueChange = onAddressChange,
                    label = { Text("Direccion") }
                )
                AppFilledTextField(
                    value = uiState.cif,
                    onValueChange = onCifChange,
                    label = { Text("CIF") }
                )
            }

            AppFormSection(title = "Adopciones") {
                Text(
                    text = "Los usuarios rellenarán este formulario cuando soliciten adoptar un animal.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                AppFilledTextField(
                    value = uiState.adoptionFormUrl,
                    onValueChange = onAdoptionFormUrlChange,
                    label = { Text("Enlace Google Forms") },
                    placeholder = { Text("https://forms.gle/...") }
                )
            }

            AppFormSection(title = "Preferencias") {
                SwitchRow(
                    text = "Alertas de adopcion",
                    icon = { Icon(Icons.Outlined.Campaign, contentDescription = null) },
                    checked = uiState.adoptionAlertsEnabled,
                    onCheckedChange = onAdoptionAlertsChange
                )
                SwitchRow(
                    text = "Modo oscuro",
                    icon = { Icon(Icons.Outlined.Palette, contentDescription = null) },
                    checked = uiState.darkModeEnabled,
                    onCheckedChange = onDarkModeChange
                )
            }

            AppFormSection(title = "Seguridad") {
                Text(
                    text = "Actualiza tu contraseña desde un formulario privado.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                AppPrimaryButton(
                    text = "Cambiar contraseña",
                    onClick = onOpenPasswordDialog
                )
            }
            AppPrimaryButton(
                text = "Guardar",
                onClick = onSaveClick
            )
            AppOutlinedButton(
                text = "Cerrar sesión",
                onClick = onLogoutClick
            )
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
            onAddressChange = {},
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
        title = { Text("Cambiar contraseña") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)) {
                PasswordField(
                    value = uiState.currentPassword,
                    onValueChange = onCurrentPasswordChange,
                    label = "Contraseña actual",
                    hidden = uiState.currentPasswordHidden,
                    onToggleVisibility = onToggleCurrentPasswordVisibility
                )
                PasswordField(
                    value = uiState.newPassword,
                    onValueChange = onNewPasswordChange,
                    label = "Nueva contraseña",
                    hidden = uiState.newPasswordHidden,
                    onToggleVisibility = onToggleNewPasswordVisibility
                )
                PasswordField(
                    value = uiState.confirmNewPassword,
                    onValueChange = onConfirmNewPasswordChange,
                    label = "Repetir nueva contraseña",
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
                Text(if (uiState.isPasswordChangeLoading) "Actualizando..." else "Actualizar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                enabled = !uiState.isPasswordChangeLoading
            ) {
                Text("Cancelar")
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

