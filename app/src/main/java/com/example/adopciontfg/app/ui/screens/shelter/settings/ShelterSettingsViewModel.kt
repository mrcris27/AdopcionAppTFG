package com.example.adopciontfg.app.ui.screens.shelter.settings

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.adopciontfg.R
import com.example.adopciontfg.app.ui.validation.isValidEmail
import com.example.adopciontfg.app.ui.validation.isValidGoogleFormsUrl
import com.example.adopciontfg.app.ui.validation.isValidSpanishPhone
import com.example.adopciontfg.data.local.entity.ShelterEntity
import com.example.adopciontfg.data.repository.ShelterRepository
import com.example.adopciontfg.data.util.ShelterAddressParts
import com.example.adopciontfg.data.util.buildShelterAddress
import com.example.adopciontfg.data.util.parseShelterAddress
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
    val street: String = "",
    val streetNumber: String = "",
    val postalCode: String = "",
    val city: String = "",
    val province: String = "",
    val cif: String = "",
    val profilePhotoUri: String = "",
    val adoptionFormUrl: String = "",
    val darkModeEnabled: Boolean = false,
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmNewPassword: String = "",
    val currentPasswordHidden: Boolean = true,
    val newPasswordHidden: Boolean = true,
    val confirmNewPasswordHidden: Boolean = true,
    val isPasswordChangeDialogOpen: Boolean = false,
    val isPasswordChangeLoading: Boolean = false,
    val isSavingSettings: Boolean = false,
    val savedSettings: ShelterSettingsData? = null,
    val saveMessageRes: Int? = null,
    val saveMessage: String? = null
) {
    val hasUnsavedChanges: Boolean
        get() = savedSettings?.let { toSettingsData() != it } ?: false

    val canSaveSettings: Boolean
        get() = hasUnsavedChanges &&
            !isSavingSettings &&
            hasRequiredFields &&
            !isEmailInvalid &&
            !isPhoneInvalid &&
            !isPostalCodeInvalid &&
            !isAdoptionFormUrlInvalid

    val isPhoneInvalid: Boolean
        get() = phone.isNotBlank() && !isValidSpanishPhone(phone)

    val isEmailInvalid: Boolean
        get() = email.isNotBlank() && !isValidEmail(email)

    val isPostalCodeInvalid: Boolean
        get() = postalCode.isNotBlank() && (postalCode.length != 5 || postalCode.any { !it.isDigit() })

    val isAdoptionFormUrlInvalid: Boolean
        get() = adoptionFormUrl.isNotBlank() && !isValidGoogleFormsUrl(adoptionFormUrl)

    private val hasRequiredFields: Boolean
        get() = shelterName.isNotBlank() &&
            email.isNotBlank() &&
            phone.isNotBlank() &&
            street.isNotBlank() &&
            streetNumber.isNotBlank() &&
            postalCode.isNotBlank() &&
            city.isNotBlank() &&
            province.isNotBlank() &&
            cif.isNotBlank() &&
            adoptionFormUrl.isNotBlank()

    fun toSettingsData(): ShelterSettingsData {
        return ShelterSettingsData(
            shelterName = shelterName,
            email = email,
            phone = phone,
            address = address,
            cif = cif,
            profilePhotoUri = profilePhotoUri,
            adoptionFormUrl = adoptionFormUrl,
            darkModeEnabled = darkModeEnabled
        )
    }
}

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
                val addressParts = parseShelterAddress(data.address)
                val loadedSettings = ShelterSettingsData(
                    shelterName = data.shelterName.ifBlank { firebaseUser?.displayName.orEmpty() },
                    email = data.email.ifBlank { firebaseUser?.email.orEmpty() },
                    phone = data.phone,
                    address = data.address,
                    cif = data.cif,
                    profilePhotoUri = data.profilePhotoUri,
                    adoptionFormUrl = data.adoptionFormUrl,
                    darkModeEnabled = data.darkModeEnabled
                )
                _uiState.update { current ->
                    current.copy(
                        shelterName = loadedSettings.shelterName,
                        email = loadedSettings.email,
                        phone = loadedSettings.phone,
                        address = loadedSettings.address,
                        street = addressParts.street,
                        streetNumber = addressParts.streetNumber,
                        postalCode = addressParts.postalCode,
                        city = addressParts.city,
                        province = addressParts.province,
                        cif = loadedSettings.cif,
                        profilePhotoUri = loadedSettings.profilePhotoUri,
                        adoptionFormUrl = loadedSettings.adoptionFormUrl,
                        darkModeEnabled = loadedSettings.darkModeEnabled,
                        savedSettings = loadedSettings
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
                            val addressParts = parseShelterAddress(shelter.address.orEmpty())
                            val updated = current.copy(
                                shelterName = shelter.name.orEmpty(),
                                email = shelter.email.orEmpty(),
                                phone = shelter.phone.orEmpty(),
                                address = shelter.address.orEmpty(),
                                street = addressParts.street,
                                streetNumber = addressParts.streetNumber,
                                postalCode = addressParts.postalCode,
                                city = addressParts.city,
                                province = addressParts.province,
                                cif = shelter.cif.orEmpty(),
                                profilePhotoUri = shelter.profilePicture.orEmpty(),
                                adoptionFormUrl = shelter.adoptionFormUrl.orEmpty(),
                            )
                            updated.copy(savedSettings = updated.toSettingsData())
                        }
                    }
                }
            }
        }
    }

    fun onShelterNameChange(value: String) = _uiState.update { it.copy(shelterName = value) }
    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value) }
    fun onPhoneChange(value: String) = _uiState.update { it.copy(phone = value) }
    fun onStreetChange(value: String) = updateAddress { it.copy(street = value) }
    fun onStreetNumberChange(value: String) = updateAddress { it.copy(streetNumber = value) }
    fun onPostalCodeChange(value: String) =
        updateAddress { it.copy(postalCode = value.filter { char -> char.isDigit() }.take(5)) }
    fun onCityChange(value: String) = updateAddress { it.copy(city = value) }
    fun onProvinceChange(value: String) = updateAddress { it.copy(province = value) }
    fun onCifChange(value: String) = _uiState.update { it.copy(cif = value) }
    fun onProfilePhotoChange(value: String) = _uiState.update { it.copy(profilePhotoUri = value) }
    fun onAdoptionFormUrlChange(value: String) = _uiState.update { it.copy(adoptionFormUrl = value) }
    fun onCurrentPasswordChange(value: String) = _uiState.update { it.copy(currentPassword = value) }
    fun onNewPasswordChange(value: String) = _uiState.update { it.copy(newPassword = value) }
    fun onConfirmNewPasswordChange(value: String) = _uiState.update { it.copy(confirmNewPassword = value) }

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
            _uiState.update { it.copy(savedSettings = it.toSettingsData()) }
        }
    }

    private fun updateAddress(transform: (ShelterSettingsUiState) -> ShelterSettingsUiState) {
        _uiState.update { current ->
            val updated = transform(current)
            updated.copy(address = buildFullAddress(updated))
        }
    }

    private fun buildFullAddress(state: ShelterSettingsUiState): String {
        return buildShelterAddress(
            ShelterAddressParts(
                street = state.street,
                streetNumber = state.streetNumber,
                postalCode = state.postalCode,
                city = state.city,
                province = state.province,
            )
        )
    }

    fun onSaveClick() {
        val current = _uiState.value
        if (!current.canSaveSettings) return

        _uiState.update {
            it.copy(
                isSavingSettings = true,
                saveMessageRes = null,
                saveMessage = null
            )
        }
        viewModelScope.launch {
            try {
                settingsRepository.saveShelterSettings(current.toSettingsData())
                syncShelterToFirebase(current)
                updateFirebaseProfile(current)
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        isSavingSettings = false,
                        saveMessageRes = if (exception.message == null) {
                            R.string.unknown_error
                        } else {
                            null
                        },
                        saveMessage = exception.message
                    )
                }
            }
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
                _uiState.update { it.withMessage(R.string.settings_error_current_password_required) }
                return
            }
            current.newPassword.length < 6 -> {
                _uiState.update { it.withMessage(R.string.settings_error_password_too_short) }
                return
            }
            current.newPassword != current.confirmNewPassword -> {
                _uiState.update { it.withMessage(R.string.settings_error_password_mismatch) }
                return
            }
        }

        val user = auth.currentUser
        val email = user?.email
        if (user == null || email.isNullOrBlank()) {
            _uiState.update { it.withMessage(R.string.settings_error_no_active_session) }
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
                                saveMessageRes = R.string.settings_password_updated,
                                saveMessage = null
                            )
                        }
                    }
                    .addOnFailureListener { exception ->
                        _uiState.update {
                            it.copy(
                                isPasswordChangeLoading = false,
                                saveMessageRes = if (exception.message == null) {
                                    R.string.settings_password_update_failed
                                } else {
                                    null
                                },
                                saveMessage = exception.message
                            )
                        }
                    }
            }
            .addOnFailureListener {
                _uiState.update {
                    it.copy(
                        isPasswordChangeLoading = false,
                        saveMessageRes = R.string.settings_current_password_incorrect,
                        saveMessage = null
                    )
                }
            }
    }

    fun logout() {
        auth.signOut()
    }

    fun onMessageShown() {
        _uiState.update { it.copy(saveMessageRes = null, saveMessage = null) }
    }

    private fun updateFirebaseProfile(current: ShelterSettingsUiState) {
        val user = auth.currentUser
        if (user == null) {
            _uiState.update {
                it.withMessage(R.string.settings_changes_saved).copy(
                    isSavingSettings = false,
                    savedSettings = current.toSettingsData()
                )
            }
            return
        }

        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(current.shelterName.ifBlank { null })
            .build()

        user.updateProfile(profileUpdates)
            .addOnSuccessListener {
                _uiState.update {
                    it.withMessage(R.string.settings_changes_saved).copy(
                        isSavingSettings = false,
                        savedSettings = current.toSettingsData()
                    )
                }
            }
            .addOnFailureListener { exception ->
                _uiState.update {
                    it.copy(
                        isSavingSettings = false,
                        savedSettings = current.toSettingsData(),
                        saveMessageRes = if (exception.message == null) {
                            R.string.settings_changes_saved_device_only
                        } else {
                            null
                        },
                        saveMessage = exception.message
                    )
                }
            }
    }

    private fun ShelterSettingsUiState.withMessage(@StringRes messageRes: Int): ShelterSettingsUiState =
        copy(saveMessageRes = messageRes, saveMessage = null)
}
