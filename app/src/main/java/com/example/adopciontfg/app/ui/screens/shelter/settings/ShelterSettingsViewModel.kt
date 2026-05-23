package com.example.adopciontfg.app.ui.screens.shelter.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.adopciontfg.data.local.entity.ShelterEntity
import com.example.adopciontfg.data.repository.ShelterRepository
import com.example.adopciontfg.domain.settings.SettingsRepository
import com.example.adopciontfg.domain.settings.ShelterSettingsData
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ShelterSettingsUiState(
    val shelterName: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val cif: String = "",
    val profilePhotoUri: String = "",
    val adoptionFormUrl: String = "",
    val adoptionAlertsEnabled: Boolean = true,
    val darkModeEnabled: Boolean = false,
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmNewPassword: String = "",
    val currentPasswordHidden: Boolean = true,
    val newPasswordHidden: Boolean = true,
    val confirmNewPasswordHidden: Boolean = true,
    val isPasswordChangeDialogOpen: Boolean = false,
    val isPasswordChangeLoading: Boolean = false,
    val saveMessage: String? = null
)

@HiltViewModel
class ShelterSettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val shelterRepository: ShelterRepository,
) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val _uiState = MutableStateFlow(ShelterSettingsUiState())
    val uiState: StateFlow<ShelterSettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.shelterSettings().collectLatest { data ->
                val firebaseUser = auth.currentUser
                _uiState.update { current ->
                    current.copy(
                        shelterName = data.shelterName.ifBlank { firebaseUser?.displayName.orEmpty() },
                        email = data.email.ifBlank { firebaseUser?.email.orEmpty() },
                        phone = data.phone,
                        address = data.address,
                        cif = data.cif,
                        profilePhotoUri = data.profilePhotoUri.ifBlank {
                            firebaseUser?.photoUrl?.toString().orEmpty()
                        },
                        adoptionFormUrl = data.adoptionFormUrl,
                        adoptionAlertsEnabled = data.adoptionAlertsEnabled,
                        darkModeEnabled = data.darkModeEnabled
                    )
                }
            }
        }
        val shelterId = auth.currentUser?.uid
        if (shelterId != null) {
            viewModelScope.launch {
                shelterRepository.getShelterById(shelterId).asFlow().collectLatest { shelter ->
                    if (shelter != null) {
                        _uiState.update { current ->
                            current.copy(
                                shelterName = shelter.name.orEmpty(),
                                email = shelter.email.orEmpty(),
                                phone = shelter.phone.orEmpty(),
                                address = shelter.address.orEmpty(),
                                cif = shelter.cif.orEmpty(),
                                profilePhotoUri = shelter.profilePicture.orEmpty(),
                                adoptionFormUrl = shelter.adoptionFormUrl.orEmpty(),
                            )
                        }
                    }
                }
            }
        }
    }

    fun onShelterNameChange(value: String) = _uiState.update { it.copy(shelterName = value) }
    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value) }
    fun onPhoneChange(value: String) = _uiState.update { it.copy(phone = value) }
    fun onAddressChange(value: String) = _uiState.update { it.copy(address = value) }
    fun onCifChange(value: String) = _uiState.update { it.copy(cif = value) }
    fun onProfilePhotoChange(value: String) = _uiState.update { it.copy(profilePhotoUri = value) }
    fun onAdoptionFormUrlChange(value: String) = _uiState.update { it.copy(adoptionFormUrl = value) }
    fun onCurrentPasswordChange(value: String) = _uiState.update { it.copy(currentPassword = value) }
    fun onNewPasswordChange(value: String) = _uiState.update { it.copy(newPassword = value) }
    fun onConfirmNewPasswordChange(value: String) = _uiState.update { it.copy(confirmNewPassword = value) }
    
    fun onAdoptionAlertsChange(enabled: Boolean) = _uiState.update { it.copy(adoptionAlertsEnabled = enabled) }

    fun toggleCurrentPasswordVisibility() {
        _uiState.update { it.copy(currentPasswordHidden = !it.currentPasswordHidden) }
    }

    fun toggleNewPasswordVisibility() {
        _uiState.update { it.copy(newPasswordHidden = !it.newPasswordHidden) }
    }

    fun toggleConfirmNewPasswordVisibility() {
        _uiState.update { it.copy(confirmNewPasswordHidden = !it.confirmNewPasswordHidden) }
    }

    fun onOpenPasswordDialog() {
        _uiState.update { it.copy(isPasswordChangeDialogOpen = true) }
    }

    fun onDismissPasswordDialog() {
        _uiState.update {
            if (it.isPasswordChangeLoading) {
                it
            } else {
                it.copy(
                    currentPassword = "",
                    newPassword = "",
                    confirmNewPassword = "",
                    currentPasswordHidden = true,
                    newPasswordHidden = true,
                    confirmNewPasswordHidden = true,
                    isPasswordChangeDialogOpen = false
                )
            }
        }
    }
    
    fun onDarkModeChange(enabled: Boolean) {
        _uiState.update { it.copy(darkModeEnabled = enabled) }
        // Guardar inmediatamente el cambio de tema
        viewModelScope.launch {
            val current = _uiState.value
            settingsRepository.saveShelterSettings(
                current.toSettingsData().copy(darkModeEnabled = enabled)
            )
        }
    }

    fun onSaveClick() {
        viewModelScope.launch {
            val current = _uiState.value
            settingsRepository.saveShelterSettings(current.toSettingsData())
            syncShelterToFirebase(current)
            updateFirebaseProfile(current)
        }
    }

    private fun syncShelterToFirebase(current: ShelterSettingsUiState) {
        val userId = auth.currentUser?.uid ?: return
        val shelter = ShelterEntity(
            userId,
            current.shelterName,
            current.cif,
            current.profilePhotoUri,
            current.email,
            current.address,
            current.phone,
            current.adoptionFormUrl.trim(),
        )
        shelterRepository.saveShelter(shelter)
    }

    fun onChangePasswordClick() {
        val current = _uiState.value
        when {
            current.currentPassword.isBlank() -> {
                _uiState.update { it.copy(saveMessage = "Introduce tu contraseña actual") }
                return
            }
            current.newPassword.length < 6 -> {
                _uiState.update { it.copy(saveMessage = "La nueva contraseña debe tener al menos 6 caracteres") }
                return
            }
            current.newPassword != current.confirmNewPassword -> {
                _uiState.update { it.copy(saveMessage = "Las contraseñas nuevas no coinciden") }
                return
            }
        }

        val user = auth.currentUser
        val email = user?.email
        if (user == null || email.isNullOrBlank()) {
            _uiState.update { it.copy(saveMessage = "No hay una sesión activa") }
            return
        }

        _uiState.update { it.copy(isPasswordChangeLoading = true) }
        val credential = EmailAuthProvider.getCredential(email, current.currentPassword)
        user.reauthenticate(credential)
            .addOnSuccessListener {
                user.updatePassword(current.newPassword)
                    .addOnSuccessListener {
                        _uiState.update {
                            it.copy(
                                currentPassword = "",
                                newPassword = "",
                                confirmNewPassword = "",
                                isPasswordChangeDialogOpen = false,
                                isPasswordChangeLoading = false,
                                saveMessage = "Contraseña actualizada"
                            )
                        }
                    }
                    .addOnFailureListener { exception ->
                        _uiState.update {
                            it.copy(
                                isPasswordChangeLoading = false,
                                saveMessage = exception.message ?: "No se pudo actualizar la contraseña"
                            )
                        }
                    }
            }
            .addOnFailureListener {
                _uiState.update {
                    it.copy(
                        isPasswordChangeLoading = false,
                        saveMessage = "La contraseña actual no es correcta"
                    )
                }
            }
    }

    fun logout() {
        auth.signOut()
    }

    fun onMessageShown() {
        _uiState.update { it.copy(saveMessage = null) }
    }

    private fun updateFirebaseProfile(current: ShelterSettingsUiState) {
        val user = auth.currentUser
        if (user == null) {
            _uiState.update { it.copy(saveMessage = "Cambios guardados") }
            return
        }

        val photoUri = current.profilePhotoUri.takeIf { it.isNotBlank() }?.let(Uri::parse)
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(current.shelterName.ifBlank { null })
            .setPhotoUri(photoUri)
            .build()

        user.updateProfile(profileUpdates)
            .addOnSuccessListener {
                _uiState.update { it.copy(saveMessage = "Cambios guardados") }
            }
            .addOnFailureListener { exception ->
                _uiState.update {
                    it.copy(saveMessage = exception.message ?: "Cambios guardados solo en el dispositivo")
                }
            }
    }

    private fun ShelterSettingsUiState.toSettingsData(): ShelterSettingsData {
        return ShelterSettingsData(
            shelterName = shelterName,
            email = email,
            phone = phone,
            address = address,
            cif = cif,
            profilePhotoUri = profilePhotoUri,
            adoptionFormUrl = adoptionFormUrl,
            adoptionAlertsEnabled = adoptionAlertsEnabled,
            darkModeEnabled = darkModeEnabled
        )
    }
}
