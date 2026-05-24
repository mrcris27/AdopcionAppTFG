package com.example.adopciontfg.app.ui.screens.user.settings

import android.content.Context
import android.net.Uri
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.adopciontfg.R
import com.example.adopciontfg.data.local.entity.UserEntity
import com.example.adopciontfg.data.repository.UserRepository
import com.example.adopciontfg.domain.settings.SettingsRepository
import com.example.adopciontfg.domain.settings.UserSettingsData
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UserSettingsUiState(
    val name: String = "",
    val surname: String = "",
    val email: String = "",
    val biography: String = "",
    val profilePhotoUri: String = "",
    val notificationsEnabled: Boolean = true,
    val darkModeEnabled: Boolean = false,
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmNewPassword: String = "",
    val currentPasswordHidden: Boolean = true,
    val newPasswordHidden: Boolean = true,
    val confirmNewPasswordHidden: Boolean = true,
    val isPasswordChangeDialogOpen: Boolean = false,
    val isPasswordChangeLoading: Boolean = false,
    val saveMessageRes: Int? = null,
    val saveMessage: String? = null

)

@HiltViewModel
class UserSettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val userRepository: UserRepository,
    @ApplicationContext private val context: Context

) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val _uiState = MutableStateFlow(UserSettingsUiState())
    val uiState: StateFlow<UserSettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.userSettings().collectLatest { data ->
                val firebaseUser = auth.currentUser
                _uiState.update { current ->
                    current.copy(
                        name = data.name.ifBlank { firebaseUser?.displayName.orEmpty().substringBefore(" ") },
                        surname = data.surname.ifBlank {
                            firebaseUser?.displayName.orEmpty().substringAfter(" ", "")
                        },
                        email = data.email.ifBlank { firebaseUser?.email.orEmpty() },
                        biography = data.biography,
                        profilePhotoUri = data.profilePhotoUri.ifBlank {
                            firebaseUser?.photoUrl?.toString().orEmpty()
                        },
                        notificationsEnabled = data.notificationsEnabled,
                        darkModeEnabled = data.darkModeEnabled
                    )
                }
            }
        }
        val userId = auth.currentUser?.uid
        if (userId != null) {
            viewModelScope.launch {
                userRepository.getUserById(userId).asFlow().collectLatest { user ->
                    if (user != null) {
                        _uiState.update { current ->
                            current.copy(
                                name = user.name.orEmpty(),
                                surname = user.surname.orEmpty(),
                                email = user.email.orEmpty(),
                                biography = user.biography.orEmpty(),
                                profilePhotoUri = user.profilePicture.orEmpty(),
                            )
                        }
                    }
                }
            }
        }
    }

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value) }
    fun onSurnameChange(value: String) = _uiState.update { it.copy(surname = value) }
    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value) }
    fun onBiographyChange(value: String) = _uiState.update { it.copy(biography = value) }
    fun onProfilePhotoChange(value: String) = _uiState.update { it.copy(profilePhotoUri = value) }
    fun onCurrentPasswordChange(value: String) = _uiState.update { it.copy(currentPassword = value) }
    fun onNewPasswordChange(value: String) = _uiState.update { it.copy(newPassword = value) }
    fun onConfirmNewPasswordChange(value: String) = _uiState.update { it.copy(confirmNewPassword = value) }
    
    fun onNotificationsChange(enabled: Boolean) = _uiState.update { it.copy(notificationsEnabled = enabled) }

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
            settingsRepository.saveUserSettings(
                current.toSettingsData().copy(darkModeEnabled = enabled)
            )
        }
    }

    fun onSaveClick() {
        viewModelScope.launch {
            val current = _uiState.value
            settingsRepository.saveUserSettings(current.toSettingsData())
            syncUserToFirebase(current)
            updateFirebaseProfile(current)
        }
    }

    private fun syncUserToFirebase(current: UserSettingsUiState) {
        val userId = auth.currentUser?.uid ?: return
        val user = UserEntity(
            userId,
            current.name,
            current.surname,
            current.profilePhotoUri,
            current.email,
            current.biography,
        )
        userRepository.saveUser(user)
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
        _uiState.update { it.copy(saveMessage = "Sesión cerrada") }
        auth.signOut()
        context.getSharedPreferences("auth", Context.MODE_PRIVATE)
            .edit()
            .remove("role")
            .apply()
        _uiState.update { it.copy(saveMessage = "Sesión cerrada") }
    }

    fun onMessageShown() {
        _uiState.update { it.copy(saveMessageRes = null, saveMessage = null) }
    }

    private fun updateFirebaseProfile(current: UserSettingsUiState) {
        val user = auth.currentUser
        if (user == null) {
            _uiState.update { it.withMessage(R.string.settings_changes_saved) }
            return
        }

        val displayName = listOf(current.name, current.surname)
            .filter { it.isNotBlank() }
            .joinToString(" ")
        val photoUri = current.profilePhotoUri.takeIf { it.isNotBlank() }?.let(Uri::parse)
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(displayName.ifBlank { null })
            .setPhotoUri(photoUri)
            .build()

        user.updateProfile(profileUpdates)
            .addOnSuccessListener {
                _uiState.update { it.withMessage(R.string.settings_changes_saved) }
            }
            .addOnFailureListener { exception ->
                _uiState.update {
                    it.copy(
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

    private fun UserSettingsUiState.toSettingsData(): UserSettingsData {
        return UserSettingsData(
            name = name,
            surname = surname,
            email = email,
            biography = biography,
            profilePhotoUri = profilePhotoUri,
            notificationsEnabled = notificationsEnabled,
            darkModeEnabled = darkModeEnabled
        )
    }

    private fun UserSettingsUiState.withMessage(@StringRes messageRes: Int): UserSettingsUiState =
        copy(saveMessageRes = messageRes, saveMessage = null)
}
