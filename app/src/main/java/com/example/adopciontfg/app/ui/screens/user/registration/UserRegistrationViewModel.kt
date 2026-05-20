package com.example.adopciontfg.app.ui.screens.user.registration

import androidx.lifecycle.ViewModel
import com.example.adopciontfg.app.ui.validation.doPasswordsMatch
import com.example.adopciontfg.app.ui.validation.isValidEmail
import com.example.adopciontfg.app.ui.validation.isValidRegistrationPassword
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class UserRegistrationUiState(
    val name: String = "",
    val surname: String = "",
    val email: String = "",
    val biography: String = "",
    val profilePhotoUri: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val passwordHidden: Boolean = true,
    val confirmPasswordHidden: Boolean = true,
    val canRegister: Boolean = false
) {
    val isEmailInvalid: Boolean
        get() = email.isNotBlank() && !isValidEmail(email)

    val isPasswordInvalid: Boolean
        get() = password.isNotBlank() && !isValidRegistrationPassword(password)

    val doPasswordsNotMatch: Boolean
        get() = confirmPassword.isNotBlank() && !doPasswordsMatch(password, confirmPassword)
}

@HiltViewModel
class UserRegistrationViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(UserRegistrationUiState())
    val uiState: StateFlow<UserRegistrationUiState> = _uiState.asStateFlow()

    fun onNameChange(value: String) = updateForm { it.copy(name = value) }
    fun onSurnameChange(value: String) = updateForm { it.copy(surname = value) }
    fun onEmailChange(value: String) = updateForm { it.copy(email = value) }
    fun onBiographyChange(value: String) = updateForm { it.copy(biography = value) }
    fun onProfilePhotoChange(value: String) = _uiState.update { it.copy(profilePhotoUri = value) }
    fun onPasswordChange(value: String) = updateForm { it.copy(password = value) }
    fun onConfirmPasswordChange(value: String) = updateForm { it.copy(confirmPassword = value) }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(passwordHidden = !it.passwordHidden) }
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.update { it.copy(confirmPasswordHidden = !it.confirmPasswordHidden) }
    }

    private fun updateForm(transform: (UserRegistrationUiState) -> UserRegistrationUiState) {
        _uiState.update { current ->
            val updated = transform(current)
            updated.copy(canRegister = validate(updated))
        }
    }

    private fun validate(state: UserRegistrationUiState): Boolean {
        return state.name.isNotBlank() &&
            state.surname.isNotBlank() &&
            isValidEmail(state.email) &&
            isValidRegistrationPassword(state.password) &&
            doPasswordsMatch(state.password, state.confirmPassword)
    }
}
